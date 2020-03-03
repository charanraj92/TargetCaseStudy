package com.retail.retailAPI.controllers;

import com.retail.retailAPI.exceptions.InvalidRequestException;
import com.retail.retailAPI.exceptions.ProductNotFoundException;
import com.retail.retailAPI.exceptions.ServerException;
import com.retail.retailAPI.models.Product;
import com.retail.retailAPI.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    ProductService productService;
    private static Logger logger = LoggerFactory.getLogger(ProductController.class);


    /**
     * Retrieves product details
     *
     * @param id - productId.
     * @return - the product instance if it is found in the database
     */
    @GetMapping("{id}")
    public ResponseEntity getProductDetails(@PathVariable int id) {
        try {
            Optional<Product> product = productService.getProduct(id);
            return ResponseEntity.of(product);
        } catch (ProductNotFoundException ex) {
            logger.error("Unable to get product details for given ID - {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServerException ex) {
            logger.error("Failed to retrieve product details - {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    /**
     * Updates product details
     *
     * @param id      - this is the productId.
     * @param product - Product containing the updated price
     * @return - the product object if it is successfully updated
     */
    @PutMapping("{id}")
    public ResponseEntity updateProductDetails(@PathVariable int id, @RequestBody(required = true) Product product) {
        try {

            /* Is the product price valid? */
            if (null == product.getPrice()) {
                throw new InvalidRequestException("Product price cannot be null");
            }

            /* Does the request id match the product id? */
            if (id != product.getId()) {
                throw new InvalidRequestException("ProductId does not match the product");
            }

            /* Update product price */
            Optional<Product> updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.of(updatedProduct);

        } catch (ProductNotFoundException ex) {
            logger.error("Unable to get product details for given ID - {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServerException ex) {
            logger.error("Failed to retrieve product details - {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        } catch(InvalidRequestException ex) {
            logger.error("Invalid request - {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
