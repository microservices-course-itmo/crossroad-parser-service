package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.crossroad.parser.service.db.constants.Currency;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 4ound
 */
public class CurrencyTest {
    @Test
    public void currencyRubTest() {
        Assert.assertEquals(Currency.RUB, Currency.resolve("RUB"));
    }

    @Test
    public void currencyUnknownTest() {
        Assert.assertEquals(Currency.UNKNOWN, Currency.resolve("EUR"));
    }

    @Test
    public void getIdTest() {
        Assert.assertEquals(Currency.RUB.getId(), 1);
    }

    @Test
    public void getCodeTest() {
        Assert.assertEquals(Currency.RUB.getCode(), "RUB");
    }
}
