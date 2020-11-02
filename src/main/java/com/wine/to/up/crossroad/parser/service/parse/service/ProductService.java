package com.wine.to.up.crossroad.parser.service.parse.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 18.10.2020
 */
@Slf4j
public class ProductService {

    private final ParseService parseService;
    private final RequestsService requestsService;
    private final CrossroadParserServiceMetricsCollector metricsCollector;

    public ProductService(ParseService parseService,
                          RequestsService requestsService,
                          CrossroadParserServiceMetricsCollector metricsCollector) {
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
    }

    public Optional<List<Product>> getParsedProductList() {
        try {
            List<String> winesUrl = getWinesUrl(false);
            winesUrl.addAll(getWinesUrl(true));
            List<Product> wines = getParsedWines(winesUrl);

            log.info("We've collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());
            metricsCollector.parsedWines(wines.size());

            return Optional.of(wines);
        } catch (Exception ex) {
            log.error("Can't get parsed product list");
            metricsCollector.parsedWines(0);

            return Optional.empty();
        }
    }

    public void writeParsedProductListCsv(PrintWriter writer, List<Product> products) throws CsvException {
        StatefulBeanToCsv<Product> btcsv = new StatefulBeanToCsvBuilder<Product>(writer).build();

        btcsv.write(products);
    }

    private List<String> getWinesUrl(boolean sparkling) {
        List<String> winesUrl = new ArrayList<>();

        requestsService.getJson(sparkling,1).ifPresent(pojo -> {
            int pages = getPages(pojo);
            for (int i = 1; i <= pages; i++) {
                List<String> winesUrlFromPage = requestsService
                        .getHtml(sparkling, i)
                        .map(parseService::parseUrlsCatalogPage)
                        .orElse(Collections.emptyList());
                if (winesUrlFromPage.isEmpty()) {
                    log.warn("Page {} parsed, but no urls found", i);
                }
                winesUrl.addAll(winesUrlFromPage);
                log.info("Page {} parsed", i);
            }
        });
        log.info("Found {} urls", winesUrl.size());
        return winesUrl;
    }

    private int getPages(com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo pojo) {
        return (int) Math.ceil((double) pojo.getCount() / 30);
    }

    private List<Product> getParsedWines(List<String> winesUrl) {
        return winesUrl.parallelStream()
                .map(requestsService::getItemHtml)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(parseService::parseProductPage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
