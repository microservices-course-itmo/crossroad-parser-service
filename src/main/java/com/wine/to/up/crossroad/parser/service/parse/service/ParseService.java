package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.crossroad.parser.service.db.constants.City;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.*;

/**
 * Сервис для парсинга html страниц с каталогом и
 *
 * @author 4ound
 */
@Slf4j
public class ParseService {
    private static final String WINE_DETAILS_PARSING_DURATION_SUMMARY = "wine_details_parsing_duration";
    private static final String WINE_PAGE_PARSING_DURATION_SUMMARY = "wine_page_parsing_duration";

    private static final String MANUFACTURER_NAME = "Производитель";
    private static final String BRAND_NAME = "Торговая марка";
    private static final String COUNTRY_NAME = "Страна/регион";
    private static final String REGION_NAME = "Регион";
    private static final String CAPACITY_NAME = "Объем";
    private static final String STRENGTH_NAME = "Крепость, %";
    private static final String COLOR_NAME = "Цвет";
    private static final String SUGAR_NAME = "Сахaр";
    private static final String YEAR = "Урожай";
    private static final String GRAPE_SORT_NAME = "Сорт винограда";
    private static final String FLAVOR = "Аромат";
    private static final String TASTE = "Вкусовая гамма";

    @InjectEventLogger
    private EventLogger eventLogger;

    private final String baseUrl;

    public ParseService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private void setProductProperty(String wineName, Product.ProductBuilder productBuilder, String name, String value) {
        switch (name) {
            case MANUFACTURER_NAME:
                productBuilder.manufacturer(value);
                break;
            case BRAND_NAME:
                productBuilder.brand(value);
                break;
            case COUNTRY_NAME:
                productBuilder.country(value);
                break;
            case REGION_NAME:
                productBuilder.region(Arrays.asList(value.split(",  ")));
                break;
            case CAPACITY_NAME:
                String capacity = value.replace(" л", "");
                if (NumberUtils.isNumber(capacity)) {
                    productBuilder.capacity(Float.parseFloat(capacity));
                } else {
                    eventLogger.warn(W_FIELD_PARSING_FAILED, "capacity", capacity, wineName);
                }
                break;
            case STRENGTH_NAME:
                if (NumberUtils.isNumber(value)) {
                    productBuilder.strength(
                            Float.parseFloat(value)
                    );
                } else {
                    eventLogger.warn(W_FIELD_PARSING_FAILED, "strength", value, wineName);
                }
                break;
            case COLOR_NAME:
                productBuilder.color(value);
                break;
            case SUGAR_NAME:
                productBuilder.sugar(value);
                break;
            case GRAPE_SORT_NAME:
                productBuilder.grapeSort(Arrays.asList(value.split(",  ")));
                break;
            case YEAR:
                String[] year = value.split(" ");
                if (year.length > 0 && NumberUtils.isNumber(year[0])) {
                    productBuilder.year(Integer.parseInt(year[0]));
                } else {
                    eventLogger.warn(W_FIELD_PARSING_FAILED, "year", value, wineName);
                }
                break;
            case FLAVOR:
                productBuilder.flavor(value);
                break;
            case TASTE:
                productBuilder.taste(value);
                break;
            default:
                break;
        }
    }

    private void processProductProperty(String wineName, Product.ProductBuilder productBuilder, Element property) {
        Optional<String> name = Optional.ofNullable(
                property
                        .getElementsByClass("xf-product-new-about-section__property__name")
                        .first()
        )
                .map(Element::text);

        Optional<String> value = Optional.ofNullable(
                property
                        .getElementsByClass("xf-product-new-about-section__property__value")
                        .first()
        )
                .map(Element::text);

        if (name.isEmpty() || value.isEmpty()) {
            eventLogger.warn(
                    W_WINE_ATTRIBUTE_ABSENT,
                    wineName,
                    name.orElse(null),
                    value.orElse(null)
            );
            return;
        }

        setProductProperty(wineName, productBuilder, name.get(), value.get());
    }

    /**
     * Парсинг страницы вина.
     *
     * @return возвращает dto Optional<Product>
     */
    public Optional<Product> parseProductPage(@NonNull String html, String regionId) {
        return Metrics.timer(WINE_DETAILS_PARSING_DURATION_SUMMARY, "city", City.resolve(Integer.parseInt(regionId)).getName())
                .record(() -> {
                    Document document = Jsoup.parse(html);

                    Product.ProductBuilder productBuilder = Product.builder();

                    Optional<String> wineNameO = Optional.ofNullable(
                            document
                                    .getElementsByClass("xf-product-new__title js-product__title js-product-new-title")
                                    .first()
                    )
                            .map(Element::text);

                    if (wineNameO.isEmpty()) {
                        eventLogger.warn(W_WINE_DETAILS_PARSING_FAILED, html);
                        return Optional.empty();
                    }
                    String wineName = wineNameO.get();
                    productBuilder.name(wineName);

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
                                    () -> eventLogger.warn(W_FIELD_PARSING_FAILED, "old price", "", wineName)
                            );

                    Optional.ofNullable(
                            document
                                    .getElementsByClass("xf-product-new-about-section__description")
                                    .first()
                    )
                            .map(Element::text)
                            .ifPresentOrElse(description -> {
                                        productBuilder.description(description);
                                        if (description.toLowerCase().contains("игрист")
                                                || description.toLowerCase().contains("шампанск")
                                                || wineName.toLowerCase().contains("игрист")
                                                || wineName.toLowerCase().contains("шампанск"))
                                        {
                                            productBuilder.sparkling(true);
                                        }
                                    },
                                    () -> eventLogger.warn(W_FIELD_PARSING_FAILED, "description and sparkling", "", wineName)
                            );

                    Elements properties = document.getElementsByClass("xf-product-new-about-section__property");
                    properties.forEach(property -> processProductProperty(wineName, productBuilder, property));

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
                                    () -> eventLogger.error(W_FIELD_PARSING_FAILED, "rating", "", wineName)
                            );

                    Optional.ofNullable(
                            document
                                    .getElementsByAttributeValue("rel", "canonical")
                                    .first()
                    )
                            .map(element -> element.attr("href"))
                            .ifPresentOrElse(
                                    productBuilder::link,
                                    () -> eventLogger.error(W_FIELD_PARSING_FAILED, "link", "", wineName)
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
                                    () -> eventLogger.warn(W_FIELD_PARSING_FAILED, "image url", "", wineName)
                            );
                    eventLogger.info(I_WINE_DETAILS_PARSED, wineName);
                    document = null;
                    System.gc();
                    return Optional.of(productBuilder.build());
                });
    }

    /**
     * Парсинг страницы каталога вин.
     *
     * @return список ссылок на страницы вин
     */
    public List<String> parseUrlsCatalogPage(String html, String regionId) {
        return Metrics.timer(WINE_PAGE_PARSING_DURATION_SUMMARY, "city", City.resolve(Integer.parseInt(regionId)).getName())
                .record(() -> {
                    List<String> productsUrls = new ArrayList<>();
                    Document document = Jsoup.parse(html);
                    Optional.ofNullable(document.select(".xf-catalog__item"))
                            .ifPresentOrElse(
                                    elements -> {
                                        elements.forEach(item -> {
                                            if (item.childrenSize() > 0) {
                                                parseProductCardAndGetUrl(item.child(0)).ifPresent(productsUrls::add);
                                            }
                                        });
                                        log.info("Found {} urls on a page", elements.size());

                                    },
                                    () -> eventLogger.warn(W_WINE_PAGE_PARSING_FAILED, html));


                    return Collections.unmodifiableList(productsUrls);
                });
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
