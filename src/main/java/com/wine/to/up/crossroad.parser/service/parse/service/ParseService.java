package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author 4ound
 */
@Slf4j
public class ParseService {
    private static final int BRAND = 1;
    private static final int CAPACITY = 3;
    private static final int STRENGTH = 4;
    private static final int COLOR = 5;
    private static final int SUGAR = 6;

    /**
     * Парсинг страницы вина.
     *
     * @return возвращает dto Product
     */
    public static Optional<Product> parseProductPage(String html) {
        try {
            Document document = Jsoup.parse(html);

            String name = document
                    .getElementsByClass("xf-product-new__title js-product__title js-product-new-title")
                    .get(0)
                    .text();

            float price = Float.parseFloat(
                    document
                            .getElementsByClass("js-price-rouble")
                            .get(0)
                            .text()
            );


            Elements properties = document
                    .getElementsByClass("xf-product-new-about-section__property__value");

            String brand = properties.get(BRAND).text();

            // страны нет

            float capacity = Float.parseFloat(
                    properties.get(CAPACITY).text().replace("л", "")
            );

            float strength = Float.parseFloat(
                    properties.get(STRENGTH).text()
            );

            String color = properties.get(COLOR).text();

            String sugar = properties.get(SUGAR).text();

            return Optional.of(
                    Product.builder()
                            .name(name)
                            .brand(brand)
                            .capacity(capacity)
                            .strength(strength)
                            .color(color)
                            .sugar(sugar)
                            .price(price)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Can't parse this page");
        }
        return Optional.empty();
    }

    /**
     * Парсинг страницы каталога вин.
     *
     * @return список ссылок на страницы вин
     */
    public static List<String> parseUrlsCatalogPage(String html) {
        List<String> productsUrls = new ArrayList<>();

        try { //TODO make try/catch more granular
            Document document = Jsoup.parse(html);
            Element catalogItems = document.getElementById("catalogItems");
            for (Element item : catalogItems.children()) {
                productsUrls.add(
                        parseProductCardAndGetUrl(item.child(0))
                );
            }
        } catch (Exception e) {
            log.warn("Can't parse this page");
        }

        return Collections.unmodifiableList(productsUrls);
    }


    /**
     * Парсинг карточки вина.
     *
     * @return ссылку на полную страницу вина
     */
    private static String parseProductCardAndGetUrl(Element productCard) {
        return productCard
                .getElementsByClass("xf-product__title").get(0)
                .getElementsByTag("a")
                .attr("href");
    }
}
