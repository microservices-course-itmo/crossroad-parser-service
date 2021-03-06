package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.crossroad.parser.service.db.constants.City;
import com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo;
import com.wine.to.up.crossroad.parser.service.proxy.Proxy;
import com.wine.to.up.crossroad.parser.service.proxy.ProxyFeignClient;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.*;

@Slf4j
@Getter
@Setter
@ToString
public class RequestsService {
    private static final String WINE_DETAILS_FETCHING_DURATION_SUMMARY = "wine_details_fetching_duration";
    private static final String WINE_PAGE_FETCHING_DURATION_SUMMARY = "wine_page_fetching_duration";
    private static final String REGION_PARAMETER = "region";

    private final String baseUrl;
    private final String userAgent;
    private final int timeout;
    private final String defaultRegion;
    private final ProxyFeignClient proxyService;

    private List<Proxy> proxyList;

    @InjectEventLogger
    private EventLogger eventLogger;

    public RequestsService(ProxyFeignClient proxyService, String baseUrl, String userAgent, int timeout, String defaultRegion) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.defaultRegion = defaultRegion;
        this.proxyService = Objects.requireNonNull(proxyService, "Can't get proxyService");

        this.updateProxyList();
    }

    public void updateProxyList() {
        this.proxyList = proxyService.getProxies("CROSSROAD-PARSER-SERVICE");
    }

    private Proxy getProxy() {
        if (!this.proxyList.isEmpty()) {
            Random rand = new Random();
            return this.proxyList.get(rand.nextInt(this.proxyList.size()));
        } else {
            return null;
        }
    }

    private Optional<String> getByUrl(String url, String region) {
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .build();

            Proxy proxy = this.getProxy();

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .setProxy(new HttpHost(proxy.getIp(), proxy.getPort()))
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(4, false))
                    .build();

            URIBuilder builder = new URIBuilder(url);
            builder.setParameter(REGION_PARAMETER, region);
            HttpGet request = new HttpGet(builder.build());

            request.setHeader(HttpHeaders.USER_AGENT, userAgent);
            HttpResponse response = client.execute(request);

            StringBuilder result = new StringBuilder();
            String line;

            InputStreamReader sr = new InputStreamReader(response.getEntity().getContent());

            try (BufferedReader rd = new BufferedReader(sr)) {
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }

            return Optional.of(result.toString());
        } catch (IOException e) {
            eventLogger.error(E_URL_FETCHING_ERROR, url, region, e);
            return Optional.empty();
        } catch (URISyntaxException e) {
            eventLogger.error(E_INVALID_URL, url, region, e);
            return Optional.empty();
        } catch (Exception e) {
            eventLogger.error(E_UNEXPECTED_ERROR, url, region, e);
            return Optional.empty();
        }
    }

    public Optional<CatalogResponsePojo> getJson(int page) {
        return getJson(false, defaultRegion, page);
    }

    public Optional<CatalogResponsePojo> getJson(boolean sparkling, String region, int page) {
        String relativeUrl = sparkling
                ? "/catalog/alkogol/shampanskoe-igristye-vina"
                : "/catalog/alkogol/vino";
        String url = baseUrl + relativeUrl + String.format("?ajax=true&page=%d", page);
        Optional<String> json = getByUrl(url, region);
        if (json.isPresent()) {
            try {
                CatalogResponsePojo result = new ObjectMapper().readValue(json.get(), CatalogResponsePojo.class);
                return Optional.of(result);
            } catch (JsonProcessingException e) {
                eventLogger.error(E_JSON_PROCESSING_ERROR, url, e);
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<String> getHtml(boolean sparkling, String regionId, int page) {
        Optional<CatalogResponsePojo> result = Metrics.timer(WINE_PAGE_FETCHING_DURATION_SUMMARY, "city", City.resolve(Integer.parseInt(regionId)).getName())
                .record(() -> getJson(sparkling, regionId, page));

        return result.map(CatalogResponsePojo::getHtml);
    }

    public Optional<String> getItemHtml(String url, String regionId) {
        return Metrics.timer(WINE_DETAILS_FETCHING_DURATION_SUMMARY, "city", City.resolve(Integer.parseInt(regionId)).getName())
                .record(() -> getByUrl(baseUrl + url, regionId));
    }
}
