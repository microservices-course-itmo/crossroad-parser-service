package com.wine.to.up.crossroad.parser.service.db.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private String brand;
    private String country;
    private float capacity;
    private float strength;
    private String color;
    private String sugar;
    private float price;
    private float oldPrice;
    private String image;
    private int discount;
    private String manufacturer;
    private String region;
    private String link;
    private String grapeSort;
    private int year;
    private String description;
    private String gastronomy;
    private String taste;
    private String flavor;
    private float rating;
}
