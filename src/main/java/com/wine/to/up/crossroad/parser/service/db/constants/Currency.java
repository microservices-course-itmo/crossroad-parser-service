package com.wine.to.up.crossroad.parser.service.db.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
public enum Currency {
    UNKNOWN(0, "UNKNOWN"),
    RUB(1, "RUB"),
    DOLLAR(2, "USD");

    private final int id;
    private final String code;

    private static final Map<String, Currency> R = Arrays.stream(
            Currency.values()).collect(Collectors.toMap(Currency::getCode, Function.identity())
    );

    Currency(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public static Currency resolve(String code) {
        return R.getOrDefault(code, Currency.UNKNOWN);
    }
}
