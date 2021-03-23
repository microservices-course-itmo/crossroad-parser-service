package com.wine.to.up.crossroad.parser.service.controller;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 01.11.2020
 */

@Slf4j
@SpringBootTest
public class ParseRepositoryTest {

    @Mock
    private ParseController parseController;

    @Before
    public void init() {
        initMocks(this);
        doAnswer(invocation -> {
            Optional<List<Product>> optionalProducts = getProductReturn();
            return optionalProducts.isPresent() ? optionalProducts.get() : new ArrayList<>();
        }).when(parseController).parseSite();
        doNothing().when(parseController).parseSiteCsv(any());
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
    public void getMockProductsByParseControllerTest() {
        List<Product> parseResult = parseController.parseSite();
        Assert.assertNotEquals(parseResult, new ArrayList<>());
        Assert.assertTrue(parseResult.size() > 0);
    }

    @Test
    public void getProductInCsvFormatByParseControllerTest() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        parseController.parseSiteCsv(response);
        Assert.assertNull(response.getErrorMessage());
        Assert.assertEquals("ISO-8859-1", response.getCharacterEncoding());
        Assert.assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

}
