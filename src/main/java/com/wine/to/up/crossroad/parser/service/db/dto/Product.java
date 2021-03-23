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
    private Float oldPrice;
    private Float newPrice;
    private String link;
    private String image;
    private String manufacturer;
    private String brand;
    private String country;
    private List<String> region;
    private Float capacity;
    private Float strength;
    private String color;
    private String sugar;
    private List<String> grapeSort;
    private Integer year;
    private String description;
    private String gastronomy;
    private String taste;
    private String flavor;
    private Float rating;
    private boolean sparkling;
    private String city;
}
