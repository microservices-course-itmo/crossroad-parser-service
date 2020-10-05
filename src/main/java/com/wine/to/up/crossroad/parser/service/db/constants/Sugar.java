package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Sugar {
    DRY(0, UpdateProducts.Product.Sugar.DRY,"Сухое"),
    MEDIUM_DRY(1, UpdateProducts.Product.Sugar.MEDIUM_DRY, "Полусухое"),
    MEDIUM(2, UpdateProducts.Product.Sugar.MEDIUM, "Полусладкое"),
    SWEET(3, UpdateProducts.Product.Sugar.SWEET, "Сладкое");

    private final int id;
    private final UpdateProducts.Product.Sugar productSugar;
    private final String sugar;

    private static final Map<String, Sugar> R = Arrays.stream(
            Sugar.values()).collect(Collectors.toMap(Sugar::getSugar, Function.identity())
    );

    Sugar(int id, UpdateProducts.Product.Sugar productSugar, String sugar) {
        this.id = id;
        this.productSugar = productSugar;
        this.sugar = sugar;
    }

    public static UpdateProducts.Product.Sugar resolve(String sugar) {
        return R.getOrDefault(sugar, Sugar.DRY).productSugar;
    }
}
