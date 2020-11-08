package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(value = Exception.class) // retries up to three times
    private String getByUrl(String url) throws Exception {
        return Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeout)
                .data("region", region)
                .ignoreContentType(true)
                .execute()
                .body();
    }

    public Optional<CatalogResponsePojo> getJson(int page) {
        return getJson(false, page);
    }

    public Optional<CatalogResponsePojo> getJson(boolean sparkling, int page) {
        String relativeUrl = sparkling
                ? "/catalog/alkogol/shampanskoe-igristye-vina"
                : "/catalog/alkogol/vino";
        String url = baseUrl + relativeUrl + String.format("?ajax=true&page=%d", page);
        try {
            String json = getByUrl(url);
            CatalogResponsePojo result = new ObjectMapper().readValue(json, CatalogResponsePojo.class);
            return Optional.of(result);
        } catch (Exception e) {
            log.error("Cannot get json response: {} {}", url, e);
            return Optional.empty();
        }
    }

    public Optional<String> getHtml(boolean sparkling, int page) {
        Optional<CatalogResponsePojo> result = getJson(sparkling, page);
        return result.map(CatalogResponsePojo::getHtml);
    }

    public Optional<String> getItemHtml(String url) {
        try {
            String result = getByUrl(baseUrl + url);
            return Optional.of(result);
        } catch (Exception e) {
            log.error("Cannot get json response: {} {}", baseUrl + url, e);
            return Optional.empty();
        }
    }
}
