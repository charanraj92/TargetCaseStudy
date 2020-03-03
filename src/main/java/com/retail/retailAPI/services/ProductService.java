package com.retail.retailAPI.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.retailAPI.config.AppConfig;
import com.retail.retailAPI.exceptions.ProductNotFoundException;
import com.retail.retailAPI.exceptions.ServerException;
import com.retail.retailAPI.models.Product;
import com.retail.retailAPI.repositories.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class ProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AppConfig appConfig;

    /**
     * Gets the product by id from local repo, aggregates with name and returns the product
     * @param id ProductId
     * @return Optional product instance
     * @throws Exception
     */
    @Cacheable("products")
    public Optional<Product> getProduct(int id) {

        /* Retrieve product from local repo */
        Optional<Product> product = retrieveProduct(id);

        if (product.isEmpty()) {
            throw new ProductNotFoundException("Unable to find the item for ID - %d", id);
        }

        /* Get product name */
        String productName = getProductName(id);

        /* Aggregate name */
        product.get().setName(productName);

        return product;
    }

    /**
     * This method calls the external Rest API to get the product details and then converting
     * the JSON String to get the product name
     *
     * @param id ProductId
     * @return product name
     * @throws Exception if unable to find the product in the database
     */
    private String getProductName(int id) {
        try {
            /* Get the URI for the given id */
            URI uri = UriComponentsBuilder.fromHttpUrl(appConfig.getRestURL())
                    .buildAndExpand(id).toUri();

            /* Call redsky api */
            String response = restTemplate.getForObject(uri, String.class);

            /* Parse and return name */
            return getName(response);
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new ServerException("Unable to create the URI for ID - %d", id);
        } catch (RestClientException ex) {
            throw new ServerException("Unable to access the provided API");
        }
    }

    /**
     * Gets the product details from the database
     *
     * @param id Product ID
     * @return An Optional product instance
     * @throws Exception in case of any issues while retrieving item from repository
     */
    private Optional<Product> retrieveProduct(int id) {
        try {
            return priceRepository.findById(id);
        } catch (IllegalArgumentException ex) {
            logger.error("Unable to find the product in the database");
            throw new ServerException("Product cannot be retrieve with given ID - %d", id);
        }
    }

    /**
     * This method is responsible for extracting the product name from the JSON String
     * that we received from the HTTP GET call
     *
     * @param response - Response from external API
     * @return name of the product
     * @throws Exception if unable to parse the response
     */
    private String getName(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            JsonNode productName = node.path("product").path("item").path("product_description").path("title");

            if(productName.isMissingNode())
                throw new ProductNotFoundException("Unable to find product in redsky API");

            return productName.toString();
        } catch (Exception ex) {
            logger.error("Unable to parse the Json String");
            throw new ServerException("Unable to Parse the response the external API");
        }
    }


    /**
     * This method is responsible for updating the product price in the database
     * by accessing the repository
     *
     * @param id      - ProductId
     * @param product - Product instance
     * @return Optional product instance
     * @throws Exception if unable to find the product in the database
     */
    @CachePut(value = "products", key = "#id")
    public Optional<Product> updateProduct(int id, Product product) {

        /* Get product from local repo */
        Optional<Product> productInDatabase = retrieveProduct(id);

        /* Check if product exists */
        if (productInDatabase.isEmpty()) {
            logger.error("Unable to find the product in the database");
            throw new ProductNotFoundException("Unable to find the product to update with Id - %d", id);
        }

        /* Update and save */
        productInDatabase.get().setPrice(product.getPrice());
        priceRepository.save(productInDatabase.get());

        return productInDatabase;
    }
}


