package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
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
@SpringBootTest
public class ExportProductListJobTest {

    @Autowired
    private ExportProductListJob exportProductListJob;
    @Autowired
    private RequestsService requestsService;
    @Autowired
    private ParseService parseService;

    @Test
    public void parseFirstPage() {
        boolean parseSparkling = true;
        List<String> winesUrlFromPage = requestsService
                .getHtml(parseSparkling, 1)
                .map(parseService::parseUrlsCatalogPage)
                .orElse(Collections.emptyList());
        Assert.assertTrue(winesUrlFromPage.size() > 0);

        List<Product> wines = winesUrlFromPage.parallelStream()
                .map(requestsService::getItemHtml)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(parseService::parseProductPage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        int name = 0,
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
            oldPrice = 0,
            rating = 0,
            sparkling = 0;


        for (Product product : wines) {
            name += isNotNullable(product.getName());
            brand += isNotNullable(product.getBrand());
            country += isNotNullable(product.getCountry());
            region += isNotNullable(product.getRegion());
            capacity += isNotZero(product.getCapacity());
            strength += isNotZero(product.getStrength());
            color += isNotNullable(product.getColor());
            sugar += isNotNullable(product.getSugar());
            price += isNotZero(product.getNewPrice());
            oldPrice += isNotZero(product.getOldPrice());
            rating += isNotZero(product.getRating());
            image += isNotNullable(product.getImage());
            grape_sort += isNotNullable(product.getGrapeSort());
            description += isNotNullable(product.getDescription());
            sparkling += product.isSparkling() ? 1 : 0;
        }

        log.info(
                "Successfully parsed:" +
                        "\nnames: {}" +
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
                        "\nsparkling: {}",
                name, brand, country, region, capacity, strength, color, sugar, price, image, grape_sort, description, sparkling
        );

        Validate.isTrue(name > 0);
        Validate.isTrue(brand > 0);
        Validate.isTrue(country > 0);
        Validate.isTrue(region > 0);
        Validate.isTrue(capacity > 0);
        Validate.isTrue(strength > 0);
        Validate.isTrue(color > 0);
        Validate.isTrue(sugar > 0);
        Validate.isTrue(price > 0);
        Validate.isTrue(oldPrice == 0);
        Validate.isTrue(rating > 0);
        Validate.isTrue(image > 0);
        Validate.isTrue(grape_sort > 0);
        Validate.isTrue(description > 0);
        if (parseSparkling) {
            Validate.isTrue(sparkling > 0);
        }
    }

    @Test
    @Ignore
    public void shouldTrueBecauseTestRun() {
        exportProductListJob.runJob();
    }

    private <T> int isNotNullable(T t) {
        return castBoolToInt(t != null);
    }

    private float isNotZero(float value) {
        return castBoolToInt(value != 0.);
    }

    private int castBoolToInt(boolean expression) {
        return expression ? 1 : 0;
    }
}