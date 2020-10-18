package com.wine.to.up.crossroad.parser.service.configuration;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class JobConfiguration {

    @Bean
    ExportProductListJob exportProductListJob(ProductService productService,
                                              KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService) {
        return new ExportProductListJob(productService, kafkaSendMessageService);
    }
}


