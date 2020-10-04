package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.Optional;

@Slf4j
@Getter
@Setter
@ToString
public class RequestsService {
    private final String baseUrl;
    private final String userAgent;
    private final int timeout;
    private final String region;

    private final static String HEADER_REGION = "region";

    public RequestsService(String baseUrl, String userAgent, int timeout, String region) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.region = region;
    }

    public Optional<CatalogResponsePojo> getJson(int page, boolean ajax) {
        try {
            String json = Jsoup.connect(baseUrl + String.format("/catalog/alkogol/vino?page=%d&ajax=%b", page, ajax))
                    .userAgent(userAgent)
                    .timeout(timeout)
                    .data("region", region)
                    .ignoreContentType(true)
                    .execute().body();
            CatalogResponsePojo result = new ObjectMapper().readValue(json, CatalogResponsePojo.class);
            return Optional.of(result);
        }
        catch (Exception e) {
            log.error("Cannot get json response: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getHtml(int page, boolean ajax) {
        Optional<CatalogResponsePojo> result = getJson(page, ajax);
        return result.map(CatalogResponsePojo::getHtml);
    }

    public Optional<String> getItemHtml(String url) {
        try {
            String result = Jsoup.connect(baseUrl + url)
                    .userAgent(userAgent)
                    .timeout(timeout)
                    .data("region", region)
                    .ignoreContentType(true)
                    .execute().body();
            return Optional.of(result);
        }
        catch (Exception e) {
            log.error("Cannot get json response: " + e.getMessage());
            return Optional.empty();
        }
    }
}
