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
    W_WINE_PAGE_PARSING_FAILED("Can't parse wine page {}"),
    W_WINE_DETAILS_PARSING_FAILED("Can't parse wine details {}"),
    W_WINE_ATTRIBUTE_ABSENT("Can't get one attribute of wine {}, name: {}, value: {}"),
    W_PARSED_BUT_NO_URLS("Page {} parsed, but no urls found"),

    I_START_JOB("Start run job method at {}"),
    I_END_JOB("End run job method at {}; duration = {}"),
    I_WINES_PAGE_PARSED("Wines page {} parsed"),
    I_WINE_DETAILS_PARSED("Wine details parsed: {}");

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
