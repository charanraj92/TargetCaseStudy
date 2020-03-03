package com.retail.retailAPI.models;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This is the data model for the Price of the product
 */
public class Price {

    private double value;

    @Field("currency_code")
    private String currencyCode;

    public Price(double value, String currencyCode) {
        this.value = value;
        this.currencyCode = currencyCode;
    }

    public double getValue() {
        return value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
