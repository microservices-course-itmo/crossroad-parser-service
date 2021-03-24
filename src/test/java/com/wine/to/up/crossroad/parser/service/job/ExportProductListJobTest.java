package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@Slf4j
@SpringBootTest
public class ExportProductListJobTest {

    private ExportProductListJob exportProductListJob;

    @Before
    public void init() {
        exportProductListJob = mock(ExportProductListJob.class);
        doCallRealMethod().when(exportProductListJob).getProtobufProduct(isA(Product.class));
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