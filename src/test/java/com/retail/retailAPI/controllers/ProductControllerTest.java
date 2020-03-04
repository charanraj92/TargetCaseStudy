package com.retail.retailAPI.controllers;

import com.retail.retailAPI.exceptions.ProductNotFoundException;
import com.retail.retailAPI.exceptions.ServerException;
import com.retail.retailAPI.models.Price;
import com.retail.retailAPI.models.Product;
import com.retail.retailAPI.services.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ProductService productService;

    @Before
    public void setUp() {
        for (String cache : cacheManager.getCacheNames()) {
            cacheManager.getCache(cache).clear();
        }
    }

    @Test
    public void when_ProductReturned_Expect_RetrieveSuccess() throws Exception {
        Product product = new Product(1, "test", new Price(1, "USD"));
        when(productService.getProduct(anyInt())).thenReturn(Optional.of(product));

        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/products/10")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void when_ProductNotFound_Expect_RetrieveFailure() throws Exception {
        when(productService.getProduct(anyInt())).thenThrow(mock(ProductNotFoundException.class));

        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/products/10")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is4xxClientError());
    }

    @Test
    public void when_InternalErrorOccurs_Expect_RetrieveFailure() throws Exception{
        when(productService.getProduct(anyInt())).thenThrow(mock(ServerException.class));

        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/products/10")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is5xxServerError());
    }

    @WithMockUser(value = "admin", password = "{noop}password", roles = {"ADMIN"})
    @Test
    public void when_UpdateSuccess_Expect_UpdateSuccess() throws Exception {
        Product product = new Product(1, "test", new Price(1, "USD"));
        when(productService.updateProduct(anyInt(), any(Product.class)))
                .thenReturn(Optional.of(product));

        RequestBuilder builder = MockMvcRequestBuilders.put("/api/v1/products/10")
                .content("{\n" +
                        "  \"id\": 10,\n" +
                        "  \"name\": \"\\\"test\\\"\",\n" +
                        "  \"price\": {\n" +
                        "    \"value\": 34,\n" +
                        "    \"currencyCode\": \"USD\"\n" +
                        "  }\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(value = "admin", password = "{noop}password", roles = {"ADMIN"})
    @Test
    public void when_ProductNotFound_Expect_UpdateFailure() throws Exception {
        when(productService.updateProduct(anyInt(), any(Product.class)))
                .thenThrow(mock(ProductNotFoundException.class));

        RequestBuilder builder = MockMvcRequestBuilders.put("/api/v1/products/10")
                .content("{\n" +
                "  \"id\": 10,\n" +
                "  \"name\": \"test\",\n" +
                "  \"price\": {\n" +
                "    \"value\": 34,\n" +
                "    \"currencyCode\": \"USD\"\n" +
                "  }\n" +
                "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is4xxClientError());
    }

    @WithMockUser(value = "admin", password = "{noop}password", roles = {"ADMIN"})
    @Test
    public void when_InternalServerError_Expect_UpdateFailure() throws Exception {
        when(productService.updateProduct(anyInt(), any(Product.class)))
                .thenThrow(mock(ServerException.class));

        RequestBuilder builder = MockMvcRequestBuilders.put("/api/v1/products/10").content("{\n" +
                "  \"id\": 10,\n" +
                "  \"name\": \"test\",\n" +
                "  \"price\": {\n" +
                "    \"value\": 34,\n" +
                "    \"currencyCode\": \"USD\"\n" +
                "  }\n" +
                "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder).andExpect(status().is5xxServerError());
    }
}
