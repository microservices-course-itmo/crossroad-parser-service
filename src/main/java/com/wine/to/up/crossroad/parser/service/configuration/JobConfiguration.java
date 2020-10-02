package com.wine.to.up.crossroad.parser.service.configuration;

import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
@Import({ParserConfiguration.class})
public class JobConfiguration {

    @Bean
    ExportProductListJob exportProductListJob(RequestsService requestsService, ParseService parseService) {
        return new ExportProductListJob(requestsService, parseService);
    }
}


