package com.wine.to.up.crossroad.parser.service.configuration;

import com.wine.to.up.crossroad.parser.service.proxy.ProxyFeignClient;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {
    @Bean
    public ProxyFeignClient getProxyClient(Decoder decoder, Encoder encoder, Client client) {
        return Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .client(client)
            .target(ProxyFeignClient.class, "http://test");
    }
}
