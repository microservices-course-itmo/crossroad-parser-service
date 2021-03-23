package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@Slf4j
@RunWith(SpringRunner.class)
@Ignore("Integration test")
@SpringBootTest
public class ExportProductListJobTest {

    @Autowired
    private ExportProductListJob exportProductListJob;
    @Autowired
    private RequestsService requestsService;
    @Autowired
    private ParseService parseService;

    @Ignore("Integration test")
    @Test
    public void parseFirstPageSparkling() {
        parseFirstPage(true);
    }

    @Ignore("Integration test")
    @Test
    public void parseFirstPageNonSparkling() {
        parseFirstPage(false);
    }

    private void parseFirstPage(boolean parseSparkling) {
        final String parsingRegion = "2";
        List<String> winesUrlFromPage = requestsService
                .getHtml(parseSparkling, parsingRegion, 1)
                .map(parseService::parseUrlsCatalogPage)
                .orElse(Collections.emptyList());
        Assert.assertTrue(winesUrlFromPage.size() > 0);

        List<Product> wines = winesUrlFromPage.parallelStream()
                .map(url -> requestsService.getItemHtml(url, parsingRegion))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(parseService::parseProductPage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        int name = 0,
            manufacturer = 0,
            brand = 0,
            country = 0,
            region = 0,
            capacity = 0,
            strength = 0,
            color = 0,
            sugar = 0,
            price = 0,
            image = 0,
            grape_sort = 0,
            description = 0,
            rating = 0,
            sparkling = 0,
            flavor = 0,
            taste = 0;


        for (Product product : wines) {
            name += isNotNullable(product.getName());
            manufacturer += isNotNullable(product.getManufacturer());
            brand += isNotNullable(product.getBrand());
            country += isNotNullable(product.getCountry());
            region += isNotNullable(product.getRegion());
            capacity += isNotZero(product.getCapacity());
            strength += isNotZero(product.getStrength());
            color += isNotNullable(product.getColor());
            sugar += isNotNullable(product.getSugar());
            price += isNotZero(product.getNewPrice());
            rating += isNotZero(product.getRating());
            image += isNotNullable(product.getImage());
            grape_sort += isNotNullable(product.getGrapeSort());
            description += isNotNullable(product.getDescription());
            sparkling += product.isSparkling() ? 1 : 0;
            flavor += isNotNullable(product.getFlavor());
            taste += isNotNullable(product.getTaste());
        }

        log.info(
                "Successfully parsed:" +
                        "\nnames: {}" +
                        "\nmanufacturers: {}" +
                        "\nbrands: {}" +
                        "\ncountries: {}" +
                        "\nregions: {}" +
                        "\ncapacities: {}" +
                        "\nstrengths: {}" +
                        "\ncolors: {}" +
                        "\nsugars: {}" +
                        "\nprices: {}" +
                        "\nimages: {}" +
                        "\ngrape_sorts: {}" +
                        "\ndescriptions: {}" +
                        "\nsparkling: {}" +
                        "\nflavors: {}" +
                        "\ntastes: {}",
                name, manufacturer, brand, country, region, capacity, strength, color, sugar, price, image, grape_sort, description, sparkling, flavor, taste
        );

        Validate.isTrue(name > 0);
        Validate.isTrue(manufacturer > 0);
        Validate.isTrue(brand > 0);
        Validate.isTrue(country > 0);
        Validate.isTrue(region > 0);
        Validate.isTrue(capacity > 0);
        Validate.isTrue(strength > 0);
        Validate.isTrue(color > 0);
        Validate.isTrue(sugar > 0);
        Validate.isTrue(price > 0);
        Validate.isTrue(rating > 0);
        Validate.isTrue(image > 0);
        Validate.isTrue(grape_sort > 0);
        Validate.isTrue(description > 0);
        if (parseSparkling) {
            Validate.isTrue(sparkling > 0);
        }
    }

    @Test
    public void getProtobufProductTest() {
        Product product = getTestProduct();
        ParserApi.Wine wine = exportProductListJob.getProtobufProduct(product);

        Assert.assertEquals("Вино Peregrino Vinedo красное полусладкое 11% 0.75л", wine.getName());
        Assert.assertEquals(319.f, wine.getOldPrice(), 0.1);
        Assert.assertEquals(319.f, wine.getNewPrice(), 0.1);
        Assert.assertEquals("https://www.vprok.ru/product/peregrino-vinedo-vino-peregr-vin-kr-psl-0-75l--379462", wine.getLink());
        Assert.assertEquals("https://www.perekrestok.ru/src/product.file/full/image/57/60/96057.jpeg", wine.getImage());
        Assert.assertEquals("", wine.getManufacturer());
        Assert.assertEquals("Peregrino Vinedo", wine.getBrand());
        Assert.assertEquals("Spain", wine.getCountry());
        Assert.assertEquals(0, wine.getRegionCount());
        Assert.assertEquals(0.75f, wine.getCapacity(), 0.01);
        Assert.assertEquals(11.f, wine.getStrength(), 0.1);
        Assert.assertEquals(ParserApi.Wine.Color.RED, wine.getColor());
        Assert.assertEquals(ParserApi.Wine.Sugar.MEDIUM, wine.getSugar());
        Assert.assertEquals(0, wine.getGrapeSortCount());
        Assert.assertEquals(0, wine.getYear());
        Assert.assertEquals("Вино Peregrino Vinedo красное полусладкое 11%...", wine.getDescription());
        Assert.assertEquals("", wine.getGastronomy());
        Assert.assertEquals("", wine.getTaste());
        Assert.assertEquals("", wine.getFlavor());
        Assert.assertEquals(5.f, wine.getRating(), 0.1);
        Assert.assertFalse(wine.getSparkling());
    }

    private Product getTestProduct() {
        return Product.builder()
                .name("Вино Peregrino Vinedo красное полусладкое 11% 0.75л")
                .oldPrice(0.f)
                .newPrice(319.f)
                .link("https://www.vprok.ru/product/peregrino-vinedo-vino-peregr-vin-kr-psl-0-75l--379462")
                .image("https://www.perekrestok.ru/src/product.file/full/image/57/60/96057.jpeg")
                .manufacturer(null)
                .brand("Peregrino Vinedo")
                .country("Spain")
                .region(null)
                .capacity(0.75f)
                .strength(11.f)
                .color("Красное")
                .sugar("Полусладкое")
                .grapeSort(null)
                .year(null)
                .description("Вино Peregrino Vinedo красное полусладкое 11%...")
                .gastronomy(null)
                .taste(null)
                .flavor(null)
                .rating(5.f)
                .sparkling(false)
                .build();
    }

    private <T> int isNotNullable(T t) {
        return castBoolToInt(t != null);
    }

    private float isNotZero(Float value) {
        return castBoolToInt(value != null && value != 0.);
    }

    private int castBoolToInt(boolean expression) {
        return expression ? 1 : 0;
    }
}