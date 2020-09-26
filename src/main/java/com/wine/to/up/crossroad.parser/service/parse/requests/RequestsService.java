package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.crossroad.parser.service.parse.serialization.ResponsePojo;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

@Slf4j
public class RequestsService {
    private final String baseUrl;
    private final String userAgent;
    private final int timeout;
    private final String region;
    private final boolean ajax;

    private final static String HEADER_REGION = "region";

    public RequestsService(String baseUrl, String userAgent, int timeout, String region, boolean ajax) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.region = region;
        this.ajax = ajax;
    }

    public RequestsService(String baseUrl, String userAgent, int timeout, String region) {
        this(baseUrl, userAgent, timeout, region, true);
    }

    //TODO kmosunoff переделать метод так, чтобы принимал
    // номер нужной страницы
    // и использовал данные из конструктора
    public static Optional<ResponsePojo> getJson(String url) {
        try {
            ResponsePojo result = new ObjectMapper().readValue(new URL(url), ResponsePojo.class);
            return Optional.of(result);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
