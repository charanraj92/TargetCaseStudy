package com.retail.retailAPI.repositories;

import com.retail.retailAPI.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

/**
 * This is the Data Access layer that accesses the database
 * to retrieve/update the product details
 */
public interface PriceRepository extends MongoRepository<Product, Integer> { }
