package com.retail.retailAPI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${endpoint}")
    private String restURL;
    public String getRestURL() {
        return restURL;
    }
}
