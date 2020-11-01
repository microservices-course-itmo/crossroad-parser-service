package com.wine.to.up.crossroad.parser.service.controller;

import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 01.11.2020
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParseControllerTest {

    @Autowired
    private ParseController parseController;
    private ParseController parseControllerMock;

    @Before
    public void init() {
        ExportProductListJob job = mock(ExportProductListJob.class);
        doNothing().when(job).runJob();
        ProductService productService = mock(ProductService.class);
        when(productService.getParsedProductList()).thenReturn(getProductReturn());
        try {
            doCallRealMethod().when(productService).writeParsedProductListCsv(isA(PrintWriter.class), isA(List.class));
        } catch (Exception ex) {
            System.err.println("Can't parse product list to csv" + ex);
        }
        CrossroadParserServiceMetricsCollector metricsCollector = mock(CrossroadParserServiceMetricsCollector.class);
        doNothing().when(metricsCollector).parseSite(isA(Double.class));
        parseControllerMock = new ParseController(job, productService, metricsCollector);
    }

    private Optional<List<Product>> getProductReturn() {
        List<Product> products = new ArrayList<>();
        Product.ProductBuilder productBuilder = Product.builder();
        productBuilder
                .name("Вино Peregrino Vinedo красное полусладкое 11% 0.75л")
                .newPrice((float) 319)
                .brand("Peregrino Vinedo")
                .capacity((float) 0.75)
                .color("Красное")
                .sugar("Полусладкое")
                .description("Вино Peregrino Vinedo красное полусладкое 11% 0.75л – столовое вино, " +
                        "изготовлено из красных сортов винограда в испанской Кастилье, славящейся " +
                        "своими древними винодельческими хозяйствами. Напиток имеет насыщенный " +
                        "темно-вишневый цвет, который радует глаз и пробуждает аппетит. " +
                        "Вкус хорошо сбалансирован, с умеренной танинностью, наполнен оттенками " +
                        "красных фруктов и ягод. Послевкусие быстрое и бархатистое. Это полусладкое " +
                        "вино отлично сочетается с блюдами из мяса, тушеным овощами, рисом. " +
                        "Оно замечательно дополнит как праздничный стол, так и ваш регулярный обед или ужин.")
                .rating((float) 5);
        products.add(productBuilder.build());
        products.add(productBuilder.build());
        return Optional.of(products);
    }

    @Test
    public void startJobByParseControllerTest() {
        parseControllerMock.parse();
    }

    @Ignore
    @Test
    public void getProductsByParseControllerTest() {
        List<Product> parseResult = parseController.parseSite();
        Assert.assertNotEquals(parseResult, new ArrayList<>());
        Assert.assertTrue(parseResult.size() > 0);
    }

    @Test
    public void getMockProductsByParseControllerTest() {
        List<Product> parseResult = parseControllerMock.parseSite();
        Assert.assertNotEquals(parseResult, new ArrayList<>());
        Assert.assertTrue(parseResult.size() > 0);
    }

    @Ignore
    @Test
    public void getProductInCsvFormatByParseControllerTest() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        parseController.parseSiteCsv(response);
        Assert.assertNull(response.getErrorMessage());
        Assert.assertEquals(response.getCharacterEncoding(), "UTF-8");
        Assert.assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void getMockProductInJsonFormatByParseControllerTest() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        parseControllerMock.parseSiteCsv(response);
        Assert.assertNull(response.getErrorMessage());
        Assert.assertEquals(response.getCharacterEncoding(), "UTF-8");
        Assert.assertEquals(response.getStatus(), HttpStatus.OK.value());
    }
}