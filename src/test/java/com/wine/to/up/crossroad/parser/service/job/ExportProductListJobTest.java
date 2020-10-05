package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.configuration.JobConfiguration;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import org.jsoup.helper.Validate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
@SpringBootTest(classes = ExportProductListJob.class)
public class ExportProductListJobTest {

    private ExportProductListJob export;
    private RequestsService requestsService;
    private ParseService parseService;

    @Before
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JobConfiguration.class);
        export = (ExportProductListJob) context.getBean("exportProductListJob");
        requestsService = (RequestsService) context.getBean("requestsService");
        parseService = (ParseService) context.getBean("parseService");
    }

    @Ignore
    @Test
    public void parseFirstPage() {
        List<String> winesUrlFromPage = requestsService
                .getHtml(1)
                .map(parseService::parseUrlsCatalogPage)
                .orElse(Collections.emptyList());
        Assert.assertTrue(winesUrlFromPage.size() > 0);

        List<Product> wines = winesUrlFromPage.stream()
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
            capacity = 0,
            strength = 0,
            color = 0,
            sugar = 0,
            price = 0;

        for (Product product : wines) {
            name += isNotNullable(product.getName());
            brand += isNotNullable(product.getBrand());
            country += isNotNullable(product.getCountry());
            capacity += isNotZero(product.getCapacity());
            strength += isNotZero(product.getStrength());
            color += isNotNullable(product.getColor());
            sugar += isNotNullable(product.getSugar());
            price += isNotZero(product.getPrice());
        }

        Validate.isTrue(name > 0);
        Validate.isTrue(brand > 0);
        Validate.isTrue(country > 0);
        Validate.isTrue(capacity > 0);
        Validate.isTrue(strength > 0);
        Validate.isTrue(color > 0);
        Validate.isTrue(sugar > 0);
        Validate.isTrue(price > 0);
    }

    @Test
    public void should_true_becauseTestRun() {
        export.runJob();
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