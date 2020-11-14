package com.wine.to.up.crossroad.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 *
 */
@Component
public class CrossroadParserServiceMetricsCollector extends CommonMetricsCollector {
    private static final String SERVICE_NAME = "crossroad_parser_service";
    private static final String PARSED_WINES_COUNT = "parsed_wines_count";
    private static final String PARSE_SITE = "parse_site";
    private static final String PARSE_SITE_CSV = "parse_site_csv";
    private static final String PRODUCT_LIST_JOB = "product_list_job";

    public CrossroadParserServiceMetricsCollector() {
        super(SERVICE_NAME);
    }

    private static final Gauge parsedWinesGauge = Gauge.build()
            .name(PARSED_WINES_COUNT)
            .help("Number of parsed wines")
            .register();

    private static final Summary parseSiteSummary = Summary.build()
            .name(PARSE_SITE)
            .help("/parse/site execution time")
            .register();

    private static final Summary parseSiteCsvSummary = Summary.build()
            .name(PARSE_SITE_CSV)
            .help("/parse/site_csv execution time")
            .register();

    private static final Summary productListJobSummary = Summary.build()
            .name(PRODUCT_LIST_JOB)
            .help("ExportProductListJob execution time")
            .register();

    public void parsedWines(int count) {
        Metrics.gauge(PARSED_WINES_COUNT, count);
        parsedWinesGauge.set(count);
    }

    public void parseSite(long time) {
        Metrics.timer(PARSE_SITE).record(time, TimeUnit.MILLISECONDS);
        parseSiteSummary.observe(time);
    }

    public void parseSiteCsv(long time) {
        Metrics.timer(PARSE_SITE_CSV).record(time, TimeUnit.MILLISECONDS);
        parseSiteCsvSummary.observe(time);
    }

    public void productListJob(long time) {
        Metrics.timer(PRODUCT_LIST_JOB).record(time, TimeUnit.MILLISECONDS);
        productListJobSummary.observe(time);
    }
}
