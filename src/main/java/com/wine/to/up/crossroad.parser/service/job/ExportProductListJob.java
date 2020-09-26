package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */

@Slf4j
public class ExportProductListJob {

    private final RequestsService requestsService;

    public ExportProductListJob(RequestsService requestsService) {
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get parseService");
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
            int pages = 1; // получить число страниц через RequestService

            List<Product> wines = new ArrayList<>();
            for (int i = 1; i <= pages; i++) {
                String html = ""; // получить html нужной страницы через RequestService
                List<Product> winesFromPage = ParseService.parseCatalogPage(html);
                if (winesFromPage.size() == 0) {
                    log.warn("Page parsed, but no products");
                }
                wines.addAll(winesFromPage);
            }

            //отдаём все вина в кафку
            log.info("We've collected {} wines", wines.size());
        } catch (Exception ex) {
            log.error("Can't export product list", ex);
        }
    }
}
