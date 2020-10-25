package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Color {
    RED(0, ParserApi.Wine.Color.RED,"Красное"),
    ROSE(1, ParserApi.Wine.Color.ROSE, "Розовое"),
    WHITE(2, ParserApi.Wine.Color.WHITE, "Белое");

    private final int id;
    private final ParserApi.Wine.Color productColor;
    private final String color;

    private static final Map<String, Color> R = Arrays.stream(
            Color.values()).collect(Collectors.toMap(Color::getColor, Function.identity())
    );

    Color(int id, ParserApi.Wine.Color productColor, String color) {
        this.id = id;
        this.productColor = productColor;
        this.color = color;
    }

    public static ParserApi.Wine.Color resolve(String color) {
        return R.getOrDefault(color, Color.RED).productColor;
    }
}
