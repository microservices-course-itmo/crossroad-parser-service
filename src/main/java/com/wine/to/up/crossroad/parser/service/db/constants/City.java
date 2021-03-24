package com.wine.to.up.crossroad.parser.service.db.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum City {
    UNDEFINED(0, "Неизвестно"),
    MOSCOW(1, "Москва"),
    SAINT_PETERSBURG(2, "Санкт-Петербург"),
    NIZHNIY_NOVGOROD(3, "Нижний Новгород"),
    ASTRAKHAN(5, "Астрахань"),
    BELGOROD(7, "Белгород"),
    VLADIMIR(8, "Владимир"),
    VOLGOGRAD(9, "Волгоград"),
    VORONEZH(10, "Воронеж"),
    KALUGA(12, "Калуга"),
    CHERKESSK(13, "Черкесск"),
    KRASNODAR(14, "Краснодар"),
    KURSK(15, "Курск"),
    LIPETSK(16, "Липецк"),
    ORENBURG(17, "Оренбург"),
    ORYOL(18, "Орёл"),
    PENZA(19, "Пенза"),
    PERM(20, "Пермь"),
    UFA(21, "Уфа"),
    YOSHKAR_OLA(22, "Йошкар-Ола"),
    SARANSK(23, "Саранск"),
    KAZAN(24, "Казань"),
    ROSTOV_ON_DON(25, "Ростов-на-Дону"),
    RYAZAN(26, "Рязань"),
    SAMARA(27, "Самара"),
    SARATOV(28, "Саратов"),
    EKATERINBURG(29, "Екатеринбург"),
    SMOLENSK(30, "Смоленск"),
    STAVROPOL(31, "Ставрополь"),
    TVER(33, "Тверь"),
    TULA(34, "Тула"),
    TYUMEN(35, "Тюмень"),
    ULYANOVSK(36, "Ульяновск"),
    KHANTY_MANSIYSK(37, "Ханты-Мансийск"),
    CHELYABINSK(38, "Челябинск"),
    CHEBOKSARY(39, "Чебоксары"),
    YAROSLAVL(40, "Ярославль"),
    PETROZAVODSK(41, "Петрозаводск"),
    IZHEVSK(42, "Ижевск"),
    BRYANSK(45, "Брянск"),
    VOLOGDA(46, "Вологда"),
    KIROV(54, "Киров"),
    VELIKIY_NOVGOROD(61, "Великий Новгород"),
    PSKOV(65, "Псков");


    private final int id;
    private final String name;

    City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, City> R = Arrays.stream(City.values())
            .collect(Collectors.toMap(City::getId, Function.identity()));

    public static City resolve(final int id) {
        return R.getOrDefault(id, City.UNDEFINED);
    }
}
