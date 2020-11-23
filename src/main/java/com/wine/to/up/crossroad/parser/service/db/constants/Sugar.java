package com.wine.to.up.crossroad.parser.service.db.constants;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Sugar {
    DRY(0, ParserApi.Wine.Sugar.DRY,"Сухое"),
    MEDIUM_DRY(1, ParserApi.Wine.Sugar.MEDIUM_DRY, "Полусухое"),
    MEDIUM(2, ParserApi.Wine.Sugar.MEDIUM, "Полусладкое"),
    SWEET(3, ParserApi.Wine.Sugar.SWEET, "Сладкое"),
    UNRECOGNIZED(4, ParserApi.Wine.Sugar.UNRECOGNIZED, "Неизвестно"),
    ;

    private final int id;
    private final ParserApi.Wine.Sugar productSugar;
    private final String name;

    private static final Map<String, Sugar> R = Arrays.stream(
            Sugar.values()).collect(Collectors.toMap(Sugar::getName, Function.identity())
    );

    Sugar(int id, ParserApi.Wine.Sugar productSugar, String name) {
        this.id = id;
        this.productSugar = productSugar;
        this.name = name;
    }

    public static ParserApi.Wine.Sugar resolve(String sugar) {
        return R.getOrDefault(sugar, Sugar.UNRECOGNIZED).productSugar;
    }
}
