package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
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

    public ExportProductListJob(RequestsService requestsService, ParseService parseService) {
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
    }

    /**
     * Джоб, выполняющий свои действия два раза в сутки.
     * В этом классе происходит взаимодействие получаемых с сайта данных и Кафки
     *
     * @return
     */
    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
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

        try {
            List<Product> wines = winesUrl.stream()
                    .map(requestsService::getItemHtml)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(parseService::parseProductPage)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            //отдаём все вина в кафку
            log.info("We've collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());
        } catch (Exception exception) {
            log.error("Can't export product list", exception);
        }
    }
}