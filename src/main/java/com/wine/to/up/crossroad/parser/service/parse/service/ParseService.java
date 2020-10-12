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
    private static final String BRAND_NAME = "Торговая марка";
    private static final String COUNTRY_NAME = "Страна/регион";
    private static final String REGION_NAME = "Регион";
    private static final String CAPACITY_NAME = "Объем";
    private static final String STRENGTH_NAME = "Крепость, %";
    private static final String COLOR_NAME = "Цвет";
    private static final String SUGAR_NAME = "Сахaр";
    private static final String YEAR = "Урожай";
    private static final String GRAPE_SORT_NAME = "Сорт винограда";

    private final String baseUrl;

    public ParseService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Парсинг страницы вина.
     *
     * @return возвращает dto Optional<Product>
     */
    public Optional<Product> parseProductPage(String html) {
        Document document;
        try {
            document = Jsoup.parse(html);
        } catch (Exception exception) {
            log.warn("Can't parse html of whole product page {}", exception.getMessage());
            return Optional.empty();
        }

        Product.ProductBuilder productBuilder = Product.builder();

        String wineName;
        try {
            wineName = document
                    .getElementsByClass("xf-product-new__title js-product__title js-product-new-title")
                    .get(0)
                    .text();
        } catch (Exception exception) {
            log.error("Can't parse name of wine {}", exception.getMessage());
            return Optional.empty();
        }
        productBuilder.name(wineName);

        float price;
        try {
            productBuilder.newPrice(
                    Float.parseFloat(
                            document
                                    .getElementsByClass("js-product__cost")
                                    .get(0)
                                    .attr("data-cost")
                    )
            );
        } catch (Exception exception) {
            log.error("Can't parse price of wine {}\n{}", wineName, exception.getMessage());
            return Optional.empty();
        }

        try {
            float oldPrice = Float.parseFloat(
                    document
                            .getElementsByClass("js-product__old-cost")
                            .get(0)
                            .attr("data-cost")
            );
            productBuilder.oldPrice(oldPrice);
        }
        catch (Exception exception) {
            log.error("Can't parse an old price {}", exception.getMessage());
        }

        Elements properties = document.getElementsByClass("xf-product-new-about-section__property");
        properties.forEach(property -> {
            String name;
            String value;
            try {
                name = property
                        .getElementsByClass("xf-product-new-about-section__property__name").get(0)
                        .text();
                value = property
                        .getElementsByClass("xf-product-new-about-section__property__value").get(0)
                        .text();
            } catch (Exception exception) {
                log.warn("Can't get name and value of one of properties {}\n{}", wineName, exception.getMessage());
                return;
            }

            switch (name) {
                case BRAND_NAME:
                    productBuilder.brand(value);
                    break;
                case COUNTRY_NAME:
                    productBuilder.country(value);
                    break;
                case REGION_NAME:
                    productBuilder.region(Collections.singletonList(value));
                    break;
                case CAPACITY_NAME:
                    try {
                        productBuilder.capacity(
                                Float.parseFloat(value.replace("л", ""))
                        );
                    } catch (NumberFormatException numberFormatException) {
                        log.error("Can't parse capacity of wine {}\n{}", wineName, numberFormatException.getMessage());
                    }
                    break;
                case STRENGTH_NAME:
                    try {
                        productBuilder.strength(
                                Float.parseFloat(value)
                        );
                    } catch (NumberFormatException numberFormatException) {
                        log.error("Can't parse strength of wine {}\n{}", wineName, numberFormatException.getMessage());
                    }
                    break;
                case COLOR_NAME:
                    productBuilder.color(value);
                    break;
                case SUGAR_NAME:
                    productBuilder.sugar(value);
                    break;
                case GRAPE_SORT_NAME:
                    productBuilder.grapeSort(Collections.singletonList(value));
                    break;
                case YEAR:
                    try {
                        int year = Integer.parseInt(value.split(" ")[0]);
                        productBuilder.year(year);
                    }
                    catch (Exception exception) {
                        log.error("Can't parse a year {}", exception.getMessage());
                    }
                    break;
            }

            try {
                float rating = document
                        .getElementsByClass("xf-product-new__rating  js-link-scroll ")
                        .get(0)
                        .getElementsByClass("xf-product-new__rating__star  _active ")
                        .size();
                productBuilder.rating(rating);
            }
            catch (Exception exception) {
                log.error("Can't get a rating {}", exception.getMessage());
            }

            try {
                String link = document
                        .getElementsByAttributeValue("rel", "canonical")
                        .get(0)
                        .attr("href");
                productBuilder.link(link);
            }
            catch (Exception exception) {
                log.error("Can't get a link {}", exception.getMessage());
            }
        });

        try {
            productBuilder.image(
                    baseUrl + document.getElementsByClass("xf-product-new-card__image-block").get(0)
                            .selectFirst("img[itemprop=image]")
                            .getElementsByAttributeValue("itemprop", "image")
                            .attr("src")
            );
        } catch (Exception exception) {
            log.warn("Can't parse image url {}\n{}", wineName, exception.getMessage());
        }

        try {
            productBuilder.description(
                    document.getElementsByClass("xf-product-new-about-section__description").get(0)
                            .text()
            );
        } catch (Exception exception) {
            log.warn("Can't get description {}\n{}", wineName, exception.getMessage());
        }

        return Optional.of(productBuilder.build());
    }

    /**
     * Парсинг страницы каталога вин.
     *
     * @return список ссылок на страницы вин
     */
    public List<String> parseUrlsCatalogPage(String html) {
        List<String> productsUrls = new ArrayList<>();
        try {
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
    private String parseProductCardAndGetUrl(Element productCard) {
        return productCard
                .getElementsByClass("xf-product__title").get(0)
                .getElementsByTag("a")
                .attr("href");
    }
}
