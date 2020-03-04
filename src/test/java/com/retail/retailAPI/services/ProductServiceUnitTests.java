package com.retail.retailAPI.services;

import com.retail.retailAPI.exceptions.ProductNotFoundException;
import com.retail.retailAPI.exceptions.ServerException;
import com.retail.retailAPI.models.Price;
import com.retail.retailAPI.models.Product;
import com.retail.retailAPI.repositories.PriceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceUnitTests {

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private PriceRepository priceRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setUp() {
        for (String cache : cacheManager.getCacheNames()) {
            cacheManager.getCache(cache).clear();
        }
    }

    @Test(expected = ProductNotFoundException.class)
    public void when_ProductNotFound_Expect_GetThrowsException() {
        when(priceRepository.findById(anyInt())).thenReturn(Optional.empty());

        productService.getProduct(4);
    }

    @Test
    public void when_ProductFound_Expect_ProductRetrieved() {
        Product product = new Product(1, "test", new Price(4, "USD"));
        when(priceRepository.findById(anyInt())).thenReturn(Optional.of(product));

        String externalProduct = "{ \"product\" : { \"item\": { \"product_description\": { \"title\": \"Test Title\"}}}}";
        when(restTemplate.getForObject(any(URI.class), any(Class.class))).thenReturn(externalProduct);

        Optional<Product> result = productService.getProduct(1);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo("Test Title");
    }

    @Test(expected = ServerException.class)
    public void when_ExternalApiError_Expect_GetThrowsException() {
        Product product = new Product(1, "test", new Price(4, "USD"));
        when(priceRepository.findById(anyInt())).thenReturn(Optional.of(product));

        when(restTemplate.getForObject(any(URI.class), any(Class.class))).thenThrow(new RestClientException("test"));

        productService.getProduct(1);
    }

    @Test(expected = ProductNotFoundException.class)
    public void when_ProductNotFound_Expect_UpdateThrowsException() {
        when(priceRepository.findById(anyInt())).thenReturn(Optional.empty());

        productService.updateProduct(4, mock(Product.class));
    }

    @Test(expected = ServerException.class)
    public void when_MongoRepoFails_Expect_UpdateThrowsException() {
        Product product = new Product(1, "test", new Price(4, "USD"));
        when(priceRepository.findById(anyInt())).thenReturn(Optional.of(product));

        when(priceRepository.save(any())).thenThrow(new IllegalArgumentException());

        productService.updateProduct(1, mock(Product.class));
    }

    @Test
    public void when_UpdateSucceeds_Expect_UpdatedProduct() {
        Product product = new Product(1, "test", new Price(4, "USD"));
        when(priceRepository.findById(anyInt())).thenReturn(Optional.of(product));

        //when(priceRepository.save(any())).thenReturn(true);

        Optional<Product> result = productService.updateProduct(1, new Product(1, "test", new Price(5, "USD")));
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getPrice().getValue()).isEqualTo(5);
    }
}
