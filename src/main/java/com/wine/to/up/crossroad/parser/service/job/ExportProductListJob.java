package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.crossroad.parser.service.db.constants.Color;
import com.wine.to.up.crossroad.parser.service.db.constants.Sugar;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private final RequestsService requestsService;
    private final ParseService parseService;
    private final KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService;


    private static final String SHOP_LINK = "perekrestok.ru";


    public ExportProductListJob(RequestsService requestsService, ParseService parseService,
                                KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService) {
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
        this.kafkaSendMessageService = Objects.requireNonNull(kafkaSendMessageService, "Can't get kafkaSendMessageService");
    }

    /**
     * Джоб, выполняющий свои действия два раза в сутки.
     * В этом классе происходит взаимодействие получаемых с сайта данных и Кафки
     *
     * @return
     */
    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
        log.info("Job started");
        List<String> winesUrl = new ArrayList<>();

        requestsService.getJson(1).ifPresent(pojo -> {
            int pages = (int) Math.ceil((double) pojo.getCount() / 30); //TODO kmosunoff вынести в отдельный метод
            for (int i = 1; i <= pages; i++) {
                List<String> winesUrlFromPage = requestsService
                        .getHtml(i)
                        .map(parseService::parseUrlsCatalogPage)
                        .orElse(Collections.emptyList());
                if (winesUrlFromPage.size() == 0) {
                    log.warn("Page {} parsed, but no urls found", i);
                }
                winesUrl.addAll(winesUrlFromPage);
            }
        });

        log.info("Found {} urls", winesUrl.size());

        try {
            List<UpdateProducts.Product> wines = winesUrl.parallelStream()
                    .map(requestsService::getItemHtml)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(parseService::parseProductPage)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::getProtobufProduct)
                    .collect(Collectors.toList());

            UpdateProducts.UpdateProductsMessage message = UpdateProducts.UpdateProductsMessage.newBuilder()
                    .setShopLink(SHOP_LINK)
                    .addAllProducts(wines)
                    .build();

            kafkaSendMessageService.sendMessage(message);
            log.info("We've collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());
        } catch (Exception exception) {
            log.error("Can't export product list", exception);
        }
    }

    private UpdateProducts.Product getProtobufProduct(Product wine) {
        UpdateProducts.Product.Sugar sugar = convertSugar(wine.getSugar());
        UpdateProducts.Product.Color color = convertColor(wine.getColor());
        var builder = UpdateProducts.Product.newBuilder();

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
        if (wine.getImage() != null) {
            builder.setImage(wine.getImage());
        }
        builder.setDiscount(wine.getDiscount());
        if (wine.getManufacturer() != null) {
            builder.setManufacturer(wine.getManufacturer());
        }
        if (wine.getRegion() != null) {
            builder.setRegion(wine.getRegion());
        }
        if (wine.getLink() != null) {
            builder.setLink(wine.getLink());
        }
        if (wine.getGrapeSort() != null) {
            builder.setGrapeSort(wine.getGrapeSort());
        }
        builder.setYear(wine.getYear());
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

    private UpdateProducts.Product.Sugar convertSugar(String value) {
        return Sugar.resolve(value);
    }

    private UpdateProducts.Product.Color convertColor(String value) {
        return Color.resolve(value);
    }

}