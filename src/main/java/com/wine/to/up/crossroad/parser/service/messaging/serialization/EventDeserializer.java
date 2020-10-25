package com.wine.to.up.crossroad.parser.service.messaging.serialization;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
/**
 * Deserializer for {@link ParserApi.WineParsedEvent}
 */
@Slf4j
public class EventDeserializer implements Deserializer<ParserApi.WineParsedEvent> {
    /**
     * {@inheritDoc}
     */

    @Override
    public ParserApi.WineParsedEvent deserialize(String topic, byte[] bytes) {
        try {
            return ParserApi.WineParsedEvent.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to deserialize message from topic: {}. {}", topic, e);
            return null;
        }
    }
}