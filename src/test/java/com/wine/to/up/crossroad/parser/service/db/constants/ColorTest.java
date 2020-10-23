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
}
