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

    private static final String PARSING_STARTED_COUNTER = "parsing_started";
    private static final String PARSING_COMPLETE_COUNTER = "parsing_complete";
    private static final String PARSING_FAILED_COUNTER = "parsing_failed";

    public CrossroadParserServiceMetricsCollector() {
        super(SERVICE_NAME);
    }

    public void incParsingStarted() {
        Metrics.counter(PARSING_STARTED_COUNTER).increment();
    }

    public void incParsingComplete() {
        Metrics.counter(PARSING_COMPLETE_COUNTER).increment();
    }

    public void incParsingFailed() {
        Metrics.counter(PARSING_FAILED_COUNTER).increment();
    }
}
