package com.wine.to.up.crossroad.parser.service.logging;

import com.wine.to.up.commonlib.logging.NotableEvent;

public enum CrossroadParserServiceNotableEvents implements NotableEvent {
    E_UNEXPECTED_ERROR("Unexpected error: {}"),
    E_RESPONSE_WRITING_ERROR("Failed to write response: {}"),
    E_INVALID_URL("Invalid URL: {}"),
    E_URL_FETCHING_ERROR("Did not manage to fetch url : {}"),
    E_JSON_PROCESSING_ERROR("Unexpected response: {}"),
    E_PRODUCT_LIST_EXPORT_ERROR("Can't export product list: {}"),
    E_EVENT_DESERIALIZATION_ERROR("Failed to deserialize message from topic: {}. {}"),
    E_PRODUCT_LIST_PARSING_ERROR("Can't get parsed product list"),

    W_FIELD_PARSING_FAILED("Can't parse {} {} of wine {}"),
    W_PAGE_PARSING_FAILED("Can't parse page"),
    W_PROPERTY_PARSING_FAILED("Can't get one property of wine {}, name present: {}, value present: {}"),
    W_PARSED_BUT_NO_URLS("Page {} parsed, but no urls found"),

    I_START_JOB("Start run job method at {}"),
    I_END_JOB("End run job method at {}; duration = {}"),
    I_PAGE_PARSED("Page {} parsed"),
    I_PRODUCT_PARSED("Product parsed: {}");

    private final String template;

    CrossroadParserServiceNotableEvents(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String getName() {
        return name();
    }


}
