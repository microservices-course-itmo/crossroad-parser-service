package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.crossroad.parser.service.parse.serialization.ResponsePojo;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

@Slf4j
public class RequestsService {
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
