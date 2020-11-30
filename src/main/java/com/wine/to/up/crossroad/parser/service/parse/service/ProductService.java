package com.wine.to.up.crossroad.parser.service.parse.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.*;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 18.10.2020
 */
@Slf4j
public class ProductService {
    private static final String PARSING_IN_PROGRESS_GAUGE = "parsing_in_progress";
    private static final String PARSING_PROCESS_DURATION_SUMMARY = "parsing_process_duration";

    private final ParseService parseService;
    private final RequestsService requestsService;
    private final CrossroadParserServiceMetricsCollector metricsCollector;

    private final AtomicInteger parsingInProgress = new AtomicInteger();
    @InjectEventLogger
    private EventLogger eventLogger;

    public ProductService(ParseService parseService,
                          RequestsService requestsService,
                          CrossroadParserServiceMetricsCollector metricsCollector) {
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");

        Metrics.gauge(PARSING_IN_PROGRESS_GAUGE, parsingInProgress);
    }

    @Timed(PARSING_PROCESS_DURATION_SUMMARY)
    public Optional<List<Product>> performParsing() {
        try {
            parsingInProgress.incrementAndGet();
            metricsCollector.incParsingStarted();

            List<String> winesUrl = getWinesUrl(false);
            winesUrl.addAll(getWinesUrl(true));
            List<Product> wines = getParsedWines(winesUrl);

            log.info("Collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());

            return Optional.of(wines);
        } catch (Exception ex) {
            eventLogger.error(E_PRODUCT_LIST_PARSING_ERROR);
            metricsCollector.incParsingFailed();

            return Optional.empty();
        } finally {
            parsingInProgress.decrementAndGet();
            metricsCollector.incParsingComplete();
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
                    eventLogger.warn(W_PARSED_BUT_NO_URLS, i);
                }
                winesUrl.addAll(winesUrlFromPage);
                eventLogger.info(I_WINES_PAGE_PARSED, i);
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
