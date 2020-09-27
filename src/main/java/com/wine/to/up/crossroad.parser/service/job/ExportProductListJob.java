package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.serialization.ResponsePojo;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
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

    public ExportProductListJob(RequestsService requestsService) {
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
    }

    /**
     * Джоб, выполняющий свои действия два раза в сутки.
     * В этом классе происходит взаимодействие получаемых с сайта данных и Кафки
     *
     * @return
     */
    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
        try {
            Optional<ResponsePojo> pojo = requestsService.getJson(1, true);
            if (pojo.isPresent()) {
                int pages = (int) Math.ceil((double) pojo.get().getCount() / 30); // получить число страниц через RequestService
                List<String> winesUrl = new ArrayList<>();
                for (int i = 1; i <= pages; i++) {
                    Optional<String> optHtml = requestsService.getHtml(i, true);
                    if (optHtml.isPresent()) {
                        List<String> winesFromPage = ParseService.parseUrlsCatalogPage(optHtml.get());
                        if (winesFromPage.size() == 0) {
                            log.warn("Page parsed, but no urls found");
                        }
                        winesUrl.addAll(winesFromPage);
                    }
                }

                List<Product> wines = winesUrl.stream()
                        // сделать вызов RequestService и достать html
                        .map(requestsService::getItemHtml)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(ParseService::parseProductPage)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                //отдаём все вина в кафку
                log.info("We've collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());
            }
        } catch (Exception ex) {
            log.error("Can't export product list", ex);
        }
    }
}
