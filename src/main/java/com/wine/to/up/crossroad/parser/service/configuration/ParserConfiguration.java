package com.wine.to.up.crossroad.parser.service.configuration;

import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import com.wine.to.up.crossroad.parser.service.proxy.ProxyFeignClient;
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
    @Value("${site.base.url}")
    private String baseUrl;
    @Value("${site.user.agent}")
    private String userAgent;
    @Value("${site.default_region}")
    private String defaultRegion;

    @Bean
    RequestsService requestsService(ProxyFeignClient proxyService) {
        return new RequestsService(proxyService, baseUrl, userAgent, timeout, defaultRegion);
    }

    @Bean
    ParseService parseService() {
        return new ParseService(baseUrl);
    }

    @Bean
    ProductService productService(ParseService parseService, RequestsService requestsService, CrossroadParserServiceMetricsCollector metricsCollector) {
        return new ProductService(parseService, requestsService, metricsCollector, defaultRegion);
    }
}
