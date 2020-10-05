package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.crossroad.parser.service.db.constants.Sugar;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import org.junit.Assert;
import org.junit.Test;

public class SugarTest {
    @Test
    public void sugarDryTest() {
        Assert.assertEquals(UpdateProducts.Product.Sugar.DRY, Sugar.resolve("Сухое"));
    }

    @Test
    public void currencySweetTest() {
        Assert.assertEquals(UpdateProducts.Product.Sugar.SWEET, Sugar.resolve("Сладкое"));
    }
}
