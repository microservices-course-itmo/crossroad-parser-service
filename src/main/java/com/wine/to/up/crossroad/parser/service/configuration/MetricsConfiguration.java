package com.wine.to.up.crossroad.parser.service.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {
    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect();
    }
}
