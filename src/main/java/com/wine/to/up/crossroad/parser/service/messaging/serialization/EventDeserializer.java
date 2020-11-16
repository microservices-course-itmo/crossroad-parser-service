package com.wine.to.up.crossroad.parser.service.messaging.serialization;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.E_EVENT_DESERIALIZATION_ERROR;

/**
 * Deserializer for {@link ParserApi.WineParsedEvent}
 */
@Slf4j
public class EventDeserializer implements Deserializer<ParserApi.WineParsedEvent> {
    /**
     * {@inheritDoc}
     */

    @InjectEventLogger
    private EventLogger eventLogger;

    @Override
    public ParserApi.WineParsedEvent deserialize(String topic, byte[] bytes) {
        try {
            return ParserApi.WineParsedEvent.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            eventLogger.error(E_EVENT_DESERIALIZATION_ERROR, topic, e);
            return null;
        }
    }
}