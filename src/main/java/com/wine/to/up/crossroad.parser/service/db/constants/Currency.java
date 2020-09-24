package com.wine.to.up.crossroad.parser.service.db.constants;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
public enum Currency {
    RUB(1, "RUB"),
    DOLLAR(2, "USD");

    private final int id;
    private final String code;

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
}
