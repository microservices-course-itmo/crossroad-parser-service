package com.wine.to.up.crossroad.parser.service.messaging;

import com.wine.to.up.commonlib.messaging.KafkaMessageHandler;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class TestTopicKafkaMessageHandler implements KafkaMessageHandler<UpdateProducts.UpdateProductsMessage> {

    private final AtomicInteger counter = new AtomicInteger(0);


    public TestTopicKafkaMessageHandler() {
    }

    @Override
    public void handle(UpdateProducts.UpdateProductsMessage message) {
        counter.incrementAndGet();
        log.info("Message received message of type {}, number of messages: {}", message.getClass().getSimpleName(), counter.get());
    }
}