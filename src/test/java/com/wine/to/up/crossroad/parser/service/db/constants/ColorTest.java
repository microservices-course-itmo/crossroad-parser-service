package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.crossroad.parser.service.db.constants.Color;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import org.junit.Assert;
import org.junit.Test;

public class ColorTest {
    @Test
    public void colorRedTest() {
        Assert.assertEquals(UpdateProducts.Product.Color.RED, Color.resolve("Красное"));
    }

    @Test
    public void colorWhiteTest() {
        Assert.assertEquals(UpdateProducts.Product.Color.WHITE, Color.resolve("Белое"));
    }
}
