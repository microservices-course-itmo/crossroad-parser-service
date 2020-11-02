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
        Assert.assertEquals(Sugar.DRY.getId(), 0);
    }

    @Test
    public void getSugarTest() {
        Assert.assertEquals(Sugar.DRY.getSugar(), "Сухое");
    }

    @Test
    public void getProductSugarTest() {
        Assert.assertEquals(Sugar.DRY.getProductSugar(), ParserApi.Wine.Sugar.DRY);
    }
}
