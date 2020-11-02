package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.junit.Assert;
import org.junit.Test;

public class ColorTest {
    @Test
    public void colorRedTest() {
        Assert.assertEquals(ParserApi.Wine.Color.RED, Color.resolve("Красное"));
    }

    @Test
    public void colorWhiteTest() {
        Assert.assertEquals(ParserApi.Wine.Color.WHITE, Color.resolve("Белое"));
    }

    @Test
    public void getIdTest() {
        Assert.assertEquals(Color.RED.getId(), 0);
    }

    @Test
    public void getColorTest() {
        Assert.assertEquals(Color.RED.getColor(), "Красное");
    }

    @Test
    public void getProductColorTest() {
        Assert.assertEquals(Color.RED.getProductColor(), ParserApi.Wine.Color.RED);
    }
}
