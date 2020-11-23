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
    WHITE(2, ParserApi.Wine.Color.WHITE, "Белое"),
    UNDEFINED(3, ParserApi.Wine.Color.UNDEFINED_COLOR, "Неизвестно"),
    ;

    private final int id;
    private final ParserApi.Wine.Color productColor;
    private final String name;

    private static final Map<String, Color> R = Arrays.stream(
            Color.values()).collect(Collectors.toMap(Color::getName, Function.identity())
    );

    Color(int id, ParserApi.Wine.Color productColor, String name) {
        this.id = id;
        this.productColor = productColor;
        this.name = name;
    }

    public static ParserApi.Wine.Color resolve(String color) {
        return R.getOrDefault(color, Color.UNDEFINED).productColor;
    }
}
