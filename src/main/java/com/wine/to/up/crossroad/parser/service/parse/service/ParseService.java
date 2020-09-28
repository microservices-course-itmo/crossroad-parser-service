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
 * Сервис для парсинга html страниц с каталогом и
 *
 * @author 4ound
 */
@Slf4j
public class ParseService {
    private static final int BRAND_INDEX = 1;
    private static final int CAPACITY_INDEX = 3;
    private static final int STRENGTH_INDEX = 4;
    private static final int COLOR_INDEX = 5;
    private static final int SUGAR_INDEX = 6;

    /**
     * Парсинг страницы вина.
     *
     * @return возвращает dto Optional<Product>
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

            String brand = properties.get(BRAND_INDEX).text();

            float capacity = Float.parseFloat(
                    properties.get(CAPACITY_INDEX).text().replace("л", "")
            );

            float strength = Float.parseFloat(
                    properties.get(STRENGTH_INDEX).text()
            );

            String color = properties.get(COLOR_INDEX).text();

            String sugar = properties.get(SUGAR_INDEX).text();

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
            log.warn("Can't parse this page: " + e.getMessage());
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
            for (Element item : document.select(".xf-catalog__item")) {
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
