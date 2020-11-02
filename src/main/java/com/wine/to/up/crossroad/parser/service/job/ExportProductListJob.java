package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.constants.Color;
import com.wine.to.up.crossroad.parser.service.db.constants.Sugar;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Time;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.stream.Collectors;

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

    private final ProductService productService;
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService;
    private final CrossroadParserServiceMetricsCollector metricsCollector;


    private static final String SHOP_LINK = "perekrestok.ru";


    public ExportProductListJob(ProductService productService,
                                KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService,
                                CrossroadParserServiceMetricsCollector metricsCollector) {
        this.productService = Objects.requireNonNull(productService, "Can't get productService");
        this.kafkaSendMessageService = Objects.requireNonNull(kafkaSendMessageService, "Can't get kafkaSendMessageService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
    }

    /**
     * Джоб, выполняющий свои действия два раза в сутки.
     * В этом классе происходит взаимодействие получаемых с сайта данных и Кафки
     *
     * @return
     */
    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
        long startTime = new Date().getTime();
        log.info("Start run job method at {}", startTime);

        try {
            Optional<List<Product>> wineDtoList = productService.getParsedProductList();
            List<ParserApi.Wine> wines = new ArrayList<>();
            if (wineDtoList.isPresent()) {
                wines = wineDtoList.get().parallelStream()
                        .map(this::getProtobufProduct)
                        .collect(Collectors.toList());
            }

            ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                    .setShopLink(SHOP_LINK)
                    .addAllWines(wines)
                    .build();

            kafkaSendMessageService.sendMessage(message);
        } catch (Exception exception) {
            log.error("Can't export product list", exception);
        }

        log.info("End run job method at {}; duration = {}", new Date().getTime(), (new Date().getTime() - startTime));
        metricsCollector.productListJob(new Date().getTime() - startTime);
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
        builder.setOldPrice(wine.getOldPrice());
        builder.setNewPrice(wine.getNewPrice());
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