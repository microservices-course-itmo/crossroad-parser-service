package com.wine.to.up.crossroad.parser.service.db.dto;

import com.wine.to.up.crossroad.parser.service.db.constants.Currency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
public class Product {
    private String name;
    private String info;
    private BigDecimal previousCost;
    private BigDecimal currentCost;
    private Currency currency;
    private String typeValue;
}
