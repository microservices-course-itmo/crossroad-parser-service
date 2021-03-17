package com.wine.to.up.crossroad.parser.service.db.entities;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "wines")
public class Wine {
    @Id
    @Column(name = "id")
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "old_price")
    private Float oldPrice;

    @Column(name = "new_price")
    private Float newPrice;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "brand")
    private String brand;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> region;

    @Column(name = "capacity")
    private Float capacity;

    @Column(name = "strength")
    private Float strength;

    @Column(name = "color")
    private String color;

    @Column(name = "sugar")
    private String sugar;

    @Column(name = "grape_sort")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> grapeSort;

    @Column(name = "year")
    private Integer year;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "gastronomy")
    private String gastronomy;

    @Column(name = "taste")
    private String taste;

    @Column(name = "flavor")
    private String flavor;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "sparkling")
    private boolean sparkling;

    public Wine() {
    }

    public Wine(
            Long id,
            String name,
            Float oldPrice,
            Float newPrice,
            String link,
            String image,
            String manufacturer,
            String brand,
            String country,
            Set<String> region,
            Float capacity,
            Float strength,
            String color,
            String sugar,
            Set<String> grapeSort,
            Integer year,
            String description,
            String gastronomy,
            String taste,
            String flavor,
            Float rating,
            boolean sparkling)
    {
        this.id = id;
        this.name = name;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.link = link;
        this.image = image;
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.country = country;
        this.region = region;
        this.capacity = capacity;
        this.strength = strength;
        this.color = color;
        this.sugar = sugar;
        this.grapeSort = grapeSort;
        this.year = year;
        this.description = description;
        this.gastronomy = gastronomy;
        this.taste = taste;
        this.flavor = flavor;
        this.rating = rating;
        this.sparkling = sparkling;
    }
    
    public static Wine fromProduct(Product product) {
        return Wine.builder()
                .name(product.getName())
                .oldPrice(product.getOldPrice())
                .newPrice(product.getNewPrice())
                .link(product.getLink())
                .image(product.getImage())
                .manufacturer(product.getManufacturer())
                .brand(product.getBrand())
                .country(product.getCountry())
                .region(product.getRegion() != null ? Set.copyOf(product.getRegion()) : null)
                .capacity(product.getCapacity())
                .strength(product.getStrength())
                .color(product.getColor())
                .sugar(product.getSugar())
                .grapeSort(product.getGrapeSort() != null ? Set.copyOf(product.getGrapeSort()) : null)
                .year(product.getYear())
                .description(product.getDescription())
                .gastronomy(product.getGastronomy())
                .taste(product.getTaste())
                .flavor(product.getFlavor())
                .rating(product.getRating())
                .sparkling(product.isSparkling())
                .build();
    }

    public static Product toProduct(Wine wine) {
        return Product.builder()
                .name(wine.getName())
                .oldPrice(wine.getOldPrice())
                .newPrice(wine.getNewPrice())
                .link(wine.getLink())
                .image(wine.getImage())
                .manufacturer(wine.getManufacturer())
                .brand(wine.getBrand())
                .country(wine.getCountry())
                .region(wine.getRegion() != null ? List.copyOf(wine.getRegion()) : null)
                .capacity(wine.getCapacity())
                .strength(wine.getStrength())
                .color(wine.getColor())
                .sugar(wine.getSugar())
                .grapeSort(wine.getGrapeSort() != null ? List.copyOf(wine.getGrapeSort()) : null)
                .year(wine.getYear())
                .description(wine.getDescription())
                .gastronomy(wine.getGastronomy())
                .taste(wine.getTaste())
                .flavor(wine.getFlavor())
                .rating(wine.getRating())
                .sparkling(wine.isSparkling())
                .build();
    }
}
