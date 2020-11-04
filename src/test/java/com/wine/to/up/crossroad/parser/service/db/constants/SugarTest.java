package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.junit.Assert;
import org.junit.Test;

public class SugarTest {
    @Test
    public void sugarDryTest() {
        Assert.assertEquals(ParserApi.Wine.Sugar.DRY, Sugar.resolve("Сухое"));
    }

    @Test
    public void currencySweetTest() {
        Assert.assertEquals(ParserApi.Wine.Sugar.SWEET, Sugar.resolve("Сладкое"));
    }

    @Test
    public void getIdTest() {
        Assert.assertEquals(0, Sugar.DRY.getId());
    }

    @Test
    public void getStringValueTest() {
        Assert.assertEquals("Сухое", Sugar.DRY.getStringValue());
    }

    @Test
    public void getProductSugarTest() {
        Assert.assertEquals(ParserApi.Wine.Sugar.DRY, Sugar.DRY.getProductSugar());
    }
}
