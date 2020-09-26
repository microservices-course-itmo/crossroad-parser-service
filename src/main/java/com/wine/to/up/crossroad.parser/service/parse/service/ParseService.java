package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.constants.Currency;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 4ound
 */
@Slf4j
public class ParseService {
    /**
     * Парсинг страницы каталога вин.
     *
     * @return список продуктов со страницы
     */
    public static List<Product> parseCatalogPage(String html) {
        List<Product> products = new ArrayList<>();

        try { //TODO make try/catch more granular
            Document document = Jsoup.parse(html);
            Element catalogItems = document.getElementById("catalogItems");
            for (Element item : catalogItems.children()) {
                products.add(
                        parseProductCard(item.child(0))
                );
            }
        } catch (Exception e) {
            log.warn("Can't parse this page");
        }

        return Collections.unmodifiableList(products);
    }

    private static Product parseProductCard(Element productCard) {
        String title = productCard
                .getElementsByClass("xf-product__title").get(0)
                .child(0)
                .attr("title");

        float price = Float.parseFloat(
                productCard
                        .getElementsByClass("xf-product__cost").get(0)
                        .getElementsByAttributeValue("itemprop", "price").get(0)
                        .text()
        );

        Currency currency = Currency.resolve(
                productCard
                        .getElementsByClass("xf-product__cost").get(0)
                        .getElementsByAttributeValue("itemprop", "priceCurrency").get(0)
                        .text()
        );

        return Product.builder()
                .name(title)
                .price(price)
                .currency(currency)
                .build();
    }
}
