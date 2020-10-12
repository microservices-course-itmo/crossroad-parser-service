package com.wine.to.up.crossroad.parser.service.db.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@Getter
@Setter
@Builder
@ToString
public class Product {
    private String name;
    private String shopLink;
    private float oldPrice;
    private float newPrice;
    private String link;
    private String image;
    private String manufacturer;
    private String brand;
    private String country;
    private List<String> region;
    private float capacity;
    private float strength;
    private String color;
    private String sugar;
    private List<String> grapeSort;
    private int year;
    private String description;
    private String gastronomy;
    private String taste;
    private String flavor;
    private float rating;
    private boolean sparkling;
}
