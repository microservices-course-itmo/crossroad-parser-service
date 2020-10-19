package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

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
    public Optional<Product> parseProductPage(@NonNull String html) {
        Document document = Jsoup.parse(html);

        Product.ProductBuilder productBuilder = Product.builder();

        Optional<String> wineNameO = Optional.ofNullable(
                document
                        .getElementsByClass("xf-product-new__title js-product__title js-product-new-title")
                        .first()
        )
                .map(Element::text);

        if (wineNameO.isEmpty()) {
            return Optional.empty();
        }
        String wineName = wineNameO.get();
        productBuilder.name(wineName);
        if (wineName.contains("игристое")) {
            productBuilder.sparkling(true);
        }

        Optional<Float> newPriceO = Optional.ofNullable(
                document
                        .getElementsByClass("js-product__cost")
                        .first()
        )
                .map(element -> element.attr("data-cost"))
                .filter(NumberUtils::isNumber)
                .map(Float::parseFloat);
        if (newPriceO.isEmpty()) {
            return Optional.empty();
        }
        productBuilder.newPrice(newPriceO.get());

        Optional.ofNullable(
                document
                        .getElementsByClass("js-product__old-cost")
                        .first()
        )
                .map(element -> element.attr("data-cost"))
                .filter(NumberUtils::isNumber)
                .map(Float::parseFloat)
                .ifPresentOrElse(
                        productBuilder::oldPrice,
                        () -> log.warn("Can't parse an old price of wine {}", wineName)
                );

        Elements properties = document.getElementsByClass("xf-product-new-about-section__property");
        properties.forEach(property -> {
            Optional<String> nameO = Optional.ofNullable(
                    property
                            .getElementsByClass("xf-product-new-about-section__property__name")
                            .first()
            )
                    .map(Element::text);

            Optional<String> valueO = Optional.ofNullable(
                    property
                            .getElementsByClass("xf-product-new-about-section__property__value")
                            .first()
            )
                    .map(Element::text);

            if (nameO.isEmpty() || valueO.isEmpty()) {
                log.warn(
                        "Can't get one property of wine {}, name present: {}, value present: {}",
                        wineName,
                        nameO.isPresent(),
                        valueO.isPresent()
                );
                return;
            }

            String name = nameO.get();
            String value = valueO.get();

            switch (name) {
                case BRAND_NAME:
                    productBuilder.brand(value);
                    break;
                case COUNTRY_NAME:
                    productBuilder.country(value);
                    break;
                case REGION_NAME:
                    productBuilder.region(Arrays.asList(value.split(", ")));
                    break;
                case CAPACITY_NAME:
                    String capacity = value.replace(" л", "");
                    if (NumberUtils.isNumber(capacity)) {
                        productBuilder.capacity(Float.parseFloat(capacity));
                    } else {
                        log.warn("Can't parse capacity: {} of wine {}", capacity, wineName);
                    }
                    break;
                case STRENGTH_NAME:
                    if (NumberUtils.isNumber(value)) {
                        productBuilder.strength(
                                Float.parseFloat(value)
                        );
                    } else {
                        log.warn("Can't parse strength: {} of wine {}", value, wineName);
                    }
                    break;
                case COLOR_NAME:
                    productBuilder.color(value);
                    break;
                case SUGAR_NAME:
                    productBuilder.sugar(value);
                    break;
                case GRAPE_SORT_NAME:
                    productBuilder.grapeSort(Arrays.asList(value.split(", ")));
                    break;
                case YEAR:
                    String[] year = value.split(" ");
                    if (year.length > 0 && NumberUtils.isNumber(year[0])) {
                        productBuilder.year(Integer.parseInt(year[0]));
                    } else {
                        log.warn("Can't parse a year {}", value);
                    }
                    break;
            }
        });

        Optional.ofNullable(
                document
                        .getElementsByClass("xf-product-new__rating  js-link-scroll ")
                        .first()
        )
                .map(element -> element.getElementsByClass("xf-product-new__rating__star  _active "))
                .map(ArrayList::size)
                .map(Float::valueOf)
                .ifPresentOrElse(
                        productBuilder::rating,
                        () -> log.error("Can't get a rating")
                );

        Optional.ofNullable(
                document
                        .getElementsByAttributeValue("rel", "canonical")
                        .first()
        )
                .map(element -> element.attr("href"))
                .ifPresentOrElse(
                        productBuilder::link,
                        () -> log.error("Can't get a link")
                );

        Optional.ofNullable(
                document.getElementsByClass("xf-product-new-card__image-block")
                        .first()
        )
                .map(element -> element.selectFirst("img[itemprop=image]"))
                .map(element -> element.getElementsByAttributeValue("itemprop", "image"))
                .map(elements -> elements.attr("src"))
                .ifPresentOrElse(
                        partUrl -> productBuilder.image(baseUrl + partUrl),
                        () -> log.warn("Can't parse image url {}", wineName)
                );

        Optional.ofNullable(
                document
                        .getElementsByClass("xf-product-new-about-section__description")
                        .first()
        )
                .map(Element::text)
                .ifPresentOrElse(
                        productBuilder::description,
                        () -> log.warn("Can't get description {}", wineName)
                );

        return Optional.of(productBuilder.build());
    }

    /**
     * Парсинг страницы каталога вин.
     *
     * @return список ссылок на страницы вин
     */
    public List<String> parseUrlsCatalogPage(String html) {
        List<String> productsUrls = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Optional.ofNullable(document.select(".xf-catalog__item"))
                .ifPresentOrElse(
                        elements -> {
                            elements.forEach(item -> {
                                if (item.childrenSize() > 0 && item.child(0).text().contains("Вино")) {
                                    parseProductCardAndGetUrl(item.child(0)).ifPresent(productsUrls::add);
                                }
                            });
                            log.info("Found {} urls on the current page", elements.size());

                        },
                        () -> log.warn("Can't parse this page")
                );


        return Collections.unmodifiableList(productsUrls);
    }

    /**
     * Парсинг карточки вина.
     *
     * @return ссылку на полную страницу вина
     */
    private Optional<String> parseProductCardAndGetUrl(Element productCard) {
        return Optional.ofNullable(
                productCard
                        .getElementsByClass("xf-product__title").first()
        )
                .map(element -> element.getElementsByTag("a"))
                .map(elements -> elements.attr("href"));
    }
}
