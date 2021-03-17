package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.constants.Color;
import com.wine.to.up.crossroad.parser.service.db.constants.Sugar;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.db.services.WineService;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.event.EventListener;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.*;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */

@Slf4j
@PropertySource("classpath:crossroad-site.properties")
public class ExportProductListJob {
    public static final int SLEEP_AFTER_READY_SECONDS = 5;
    private static final int BATCH_SIZE = 20;
    private static final int SLEEP_TIME_BETWEEN_BATCH_SECONDS = 60;
    @Value("${site.header.region}")
    private String[] regions;

    private final ProductService productService;
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService;
    private final CrossroadParserServiceMetricsCollector metricsCollector;
    private final WineService wineService;

    @InjectEventLogger
    private EventLogger eventLogger;

    private static final String SHOP_LINK = "perekrestok.ru";


    public ExportProductListJob(ProductService productService,
                                KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService,
                                CrossroadParserServiceMetricsCollector metricsCollector,
                                WineService wineService)
    {
        this.productService = Objects.requireNonNull(productService, "Can't get productService");
        this.kafkaSendMessageService = Objects.requireNonNull(kafkaSendMessageService, "Can't get kafkaSendMessageService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
        this.wineService = Objects.requireNonNull(wineService, "Can't get wineService");
    }

    /**
     * Job running with start of application.
     * Load data from the website, parse and send it to Kafka in batches 23 times with a pause of 1 hour.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runJob() {
        log.info("Application started, parsing will start shortly ({} sec)", SLEEP_AFTER_READY_SECONDS);
        try {
            TimeUnit.SECONDS.sleep(SLEEP_AFTER_READY_SECONDS);
        } catch (InterruptedException interruptedException) {
            log.error("Can't sleep, immediate parsing");
        }

        long startTime = new Date().getTime();
        eventLogger.info(I_START_JOB, startTime);

        while (true) {
            try {
                List<Product> wines = new ArrayList<>();
                List<Pair<String, String>> winesUrlWithRegions = new ArrayList<>();
                for (final String region: regions) {
                    List<String> winesUrl = productService.getWinesUrl(false, region);
                    winesUrl.addAll(productService.getWinesUrl(true, region));
                    winesUrl.forEach(url -> winesUrlWithRegions.add(Pair.of(url, region)));
                }

                final int batchesCount = (int) Math.ceil((float) winesUrl.size() / BATCH_SIZE);

                for (int i = 0; i < batchesCount; i++) {
                    final int fromIndex = i * BATCH_SIZE;
                    final int toIndex = Math.min(winesUrlWithRegions.size(), (i + 1) * BATCH_SIZE);

                    List<Product> winesBatch = winesUrlWithRegions
                            .subList(fromIndex, toIndex)
                            .parallelStream()
                            .map(pair -> productService.parseWine(pair.getFirst(), pair.getSecond()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                    wines.addAll(winesBatch);

                    ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                            .setShopLink(SHOP_LINK)
                            .addAllWines(winesBatch.stream().map(this::getProtobufProduct).collect(Collectors.toList()))
                            .build();

                    kafkaSendMessageService.sendMessage(message);
                    metricsCollector.incWinesSentToKafka(toIndex - fromIndex);

                    TimeUnit.SECONDS.sleep(SLEEP_TIME_BETWEEN_BATCH_SECONDS);
                }
                saveDb(wines);
            } catch (Exception exception) {
                eventLogger.error(E_PRODUCT_LIST_EXPORT_ERROR, exception);
            }

            eventLogger.info(I_END_JOB, new Date().getTime(), (new Date().getTime() - startTime));
        }
    }

    public void saveDb(List<Product> wineDtoList) {
        try {
            wineService.deleteAll();
            wineService.saveAll(wineDtoList);
            log.info("DB: wines saved");
        } catch (Exception ex) {
            log.warn("DB: can't save wines in db", ex);
        }
    }

    public ParserApi.Wine getProtobufProduct(Product wine) {
        ParserApi.Wine.Sugar sugar = convertSugar(wine.getSugar());
        ParserApi.Wine.Color color = convertColor(wine.getColor());
        var builder = ParserApi.Wine.newBuilder();

        if (wine.getName() != null) {
            builder.setName(wine.getName());
        }
        if (wine.getBrand() != null) {
            builder.setBrand(wine.getBrand());
        }
        if (wine.getCountry() != null) {
            builder.setCountry(wine.getCountry());
        }
        builder.setCapacity(wine.getCapacity());
        builder.setStrength(wine.getStrength());
        if (color != null) {
            builder.setColor(color);
        }
        if (sugar != null) {
            builder.setSugar(sugar);
        }
        builder.setNewPrice(wine.getNewPrice());
        if (wine.getOldPrice() == 0.0f) {
            builder.setOldPrice(wine.getNewPrice());
        } else {
            builder.setOldPrice(wine.getOldPrice());
        }
        if (wine.getImage() != null) {
            builder.setImage(wine.getImage());
        }
        if (wine.getManufacturer() != null) {
            builder.setManufacturer(wine.getManufacturer());
        }
        if (wine.getRegion() != null) {
            builder.addAllRegion(wine.getRegion());
        }
        if (wine.getLink() != null) {
            builder.setLink(wine.getLink());
        }
        if (wine.getGrapeSort() != null) {
            builder.addAllGrapeSort(wine.getGrapeSort());
        }
        if (wine.getYear() != null) {
            builder.setYear(wine.getYear());
        }
        if (wine.getDescription() != null) {
            builder.setDescription(wine.getDescription());
        }
        if (wine.getGastronomy() != null) {
            builder.setGastronomy(wine.getGastronomy());
        }
        if (wine.getTaste() != null) {
            builder.setTaste(wine.getTaste());
        }
        if (wine.getFlavor() != null) {
            builder.setFlavor(wine.getFlavor());
        }
        builder.setRating(wine.getRating());
        return builder.build();
    }

    private ParserApi.Wine.Sugar convertSugar(String value) {
        return Sugar.resolve(value);
    }

    private ParserApi.Wine.Color convertColor(String value) {
        return Color.resolve(value);
    }

}
