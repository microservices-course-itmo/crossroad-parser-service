package com.wine.to.up.crossroad.parser.service.configuration;

import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@Configuration
@PropertySource("classpath:crossroad-site.properties")
public class ParserConfiguration {
    @Value("${site.connect.timeout}")
    private int timeout;
    @Value("${site.header.region}")
    private String region;
    @Value("${site.base.url}")
    private String baseUrl;
    @Value("${site.user.agent}")
    private String userAgent;

    @Bean
    RequestsService requestsService() {
        return new RequestsService(baseUrl, userAgent, timeout, region);
    }

    @Bean
    ParseService parseService() {
        return new ParseService();
    }
}
