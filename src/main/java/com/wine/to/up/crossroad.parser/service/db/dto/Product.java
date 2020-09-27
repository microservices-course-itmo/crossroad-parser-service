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
}
