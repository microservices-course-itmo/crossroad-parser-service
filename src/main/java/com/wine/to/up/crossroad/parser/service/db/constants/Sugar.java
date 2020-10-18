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
    SWEET(3, ParserApi.Wine.Sugar.SWEET, "Сладкое");

    private final int id;
    private final ParserApi.Wine.Sugar productSugar;
    private final String sugar;

    private static final Map<String, Sugar> R = Arrays.stream(
            Sugar.values()).collect(Collectors.toMap(Sugar::getSugar, Function.identity())
    );

    Sugar(int id, ParserApi.Wine.Sugar productSugar, String sugar) {
        this.id = id;
        this.productSugar = productSugar;
        this.sugar = sugar;
    }

    public static ParserApi.Wine.Sugar resolve(String sugar) {
        return R.getOrDefault(sugar, Sugar.DRY).productSugar;
    }
}
