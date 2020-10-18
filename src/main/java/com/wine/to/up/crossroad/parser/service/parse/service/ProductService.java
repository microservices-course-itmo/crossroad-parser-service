package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 18.10.2020
 */
@Slf4j
public class ProductService {

    private static final String SHOP_LINK = "perekrestok.ru";


    private final ParseService parseService;
    private final RequestsService requestsService;

    public ProductService(ParseService parseService, RequestsService requestsService) {
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
        this.requestsService = Objects.requireNonNull(requestsService, "Can't get requestsService");
    }

    public Optional<List<Product>> getParsedProductList() {
        try {
            List<String> winesUrl = getWinesUrl();
            List<Product> wines = getParsedWines(winesUrl);
            log.info("We've collected url to {} wines and successfully parsed {}", winesUrl.size(), wines.size());
            return Optional.of(wines);
        } catch (Exception ex) {
            log.error("Can't get parsed product list");
            return Optional.empty();
        }
    }

    private List<String> getWinesUrl() {
        List<String> winesUrl = new ArrayList<>();

        requestsService.getJson(1).ifPresent(pojo -> {
            int pages = getPages(pojo);
            for (int i = 1; i <= pages; i++) {
                List<String> winesUrlFromPage = requestsService
                        .getHtml(i)
                        .map(parseService::parseUrlsCatalogPage)
                        .orElse(Collections.emptyList());
                if (winesUrlFromPage.size() == 0) {
                    log.warn("Page {} parsed, but no urls found", i);
                }
                winesUrl.addAll(winesUrlFromPage);
            }
        });
        log.info("Found {} urls", winesUrl.size());
        return winesUrl;
    }

    private int getPages(com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo pojo) {
        return (int) Math.ceil((double) pojo.getCount() / 30);
    }

    private List<Product> getParsedWines(List<String> winesUrl) {
        return winesUrl.parallelStream()
                .map(requestsService::getItemHtml)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(parseService::parseProductPage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(product -> product.setShopLink(SHOP_LINK))
                .collect(Collectors.toList());
    }
}