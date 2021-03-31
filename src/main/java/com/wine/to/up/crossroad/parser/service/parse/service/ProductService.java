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
import java.util.concurrent.atomic.AtomicLong;
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
    private static final String TIME_SINCE_LAST_SUCCEEDED_PARSING_GAUGE = "time_since_last_succeeded_parsing";

    private final ParseService parseService;
    private final RequestsService requestsService;
    private final CrossroadParserServiceMetricsCollector metricsCollector;
    private final String defaultRegion;

    private final AtomicInteger parsingInProgress = new AtomicInteger(0);
    private final AtomicLong lastSucceededParsingTime = new AtomicLong(0);

    @InjectEventLogger
    private EventLogger eventLogger;

    public ProductService(ParseService parseService,
                          RequestsService requestsService,
                          CrossroadParserServiceMetricsCollector metricsCollector,
                          String defaultRegion)
    {
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
        this.defaultRegion = defaultRegion;

        Metrics.gauge(PARSING_IN_PROGRESS_GAUGE, parsingInProgress);
        Metrics.gauge(
                TIME_SINCE_LAST_SUCCEEDED_PARSING_GAUGE,
                lastSucceededParsingTime,
                val -> val.get() == 0 ? Double.NaN : (System.currentTimeMillis() - val.get()) / 1000.0
        );
    }

    @Timed(PARSING_PROCESS_DURATION_SUMMARY)
    public Optional<List<Product>> performParsing() {
        try {
            parsingInProgress.incrementAndGet();
            metricsCollector.incParsingStarted();

            List<String> winesUrl = getWinesUrl(false, defaultRegion);
            winesUrl.addAll(getWinesUrl(true, defaultRegion));
            List<Product> wines = getParsedWines(winesUrl, defaultRegion);

            log.info("Collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());

            lastSucceededParsingTime.set(System.currentTimeMillis());

            return Optional.of(wines);
        } catch (Exception ex) {
            eventLogger.error(E_PRODUCT_LIST_PARSING_ERROR, ex);
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

    public List<String> getWinesUrl(boolean sparkling, String region) {
        List<String> winesUrl = new ArrayList<>();

        requestsService.getJson(sparkling, region, 1).ifPresent(pojo -> {
            int pages = getPages(pojo);
            for (int i = 1; i <= pages; i++) {
                List<String> winesUrlFromPage = requestsService
                        .getHtml(sparkling, region, i)
                        .map((w) -> parseService.parseUrlsCatalogPage(w, region))
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

    public Optional<Product> parseWine(String wineUrl, String regionId) {
        Optional<String> html = requestsService.getItemHtml(wineUrl, regionId);
        if (html.isEmpty()) {
            return Optional.empty();
        }

        return parseService.parseProductPage(html.get(), regionId);
    }

    private List<Product> getParsedWines(List<String> winesUrl, String regionId) {
        return winesUrl.parallelStream()
                .map(url -> requestsService.getItemHtml(url, regionId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map((html) -> parseService.parseProductPage(html, regionId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
