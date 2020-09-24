package com.wine.to.up.crossroad.parser.service.parse.client;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@Slf4j
public class ParseClient {
    private final String baseUrl;
    private final String userAgent;
    private final int timeout;
    private final String region;

    private final static String HEADER_REGION = "region";

    public ParseClient(String baseUrl, String userAgent, int timeout, String region) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.region = region;
    }

    public Document getDocumentByPath(String path) {
        try {
            return Jsoup.connect(baseUrl + path)
                    .userAgent(userAgent)
                    .header(HEADER_REGION, region)
                    .timeout(timeout)
                    .get();
        } catch (Exception ex) {
            log.error("Can't connect to site by url = {} and userAgent = {}", baseUrl, userAgent, ex);
            return null;
        }
    }
}
