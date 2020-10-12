package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Color {
    RED(0, UpdateProducts.Product.Color.RED,"Красное"),
    ROSE(1, UpdateProducts.Product.Color.ROSE, "Розовое"),
    WHITE(2, UpdateProducts.Product.Color.WHITE, "Белое");

    private final int id;
    private final UpdateProducts.Product.Color productColor;
    private final String color;

    private static final Map<String, Color> R = Arrays.stream(
            Color.values()).collect(Collectors.toMap(Color::getColor, Function.identity())
    );

    Color(int id, UpdateProducts.Product.Color productColor, String color) {
        this.id = id;
        this.productColor = productColor;
        this.color = color;
    }

    public static UpdateProducts.Product.Color resolve(String color) {
        return R.getOrDefault(color, Color.RED).productColor;
    }
}
