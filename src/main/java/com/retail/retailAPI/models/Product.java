package com.retail.retailAPI.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This is the Data Model for the product
 */
@Document(collection = "products")
public class Product {

    @Id
    private int id;

    private String name;

    @Field("current_price")
    private Price price;

    public Product(int id, String name, Price price)
    {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public void setName(String productName) {
        this.name = productName;
    }

    public String getName() {
        return name;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }

}
