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
}
