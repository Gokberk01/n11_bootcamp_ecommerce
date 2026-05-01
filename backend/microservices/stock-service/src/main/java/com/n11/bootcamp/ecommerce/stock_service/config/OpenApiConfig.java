package com.n11.bootcamp.ecommerce.stock_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stockServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Service API")
                        .version("1.0.0")
                        .description("This API manages stock levels, and operations. " +
                                "It ensures real-time stock synchronization across the e-commerce microservices."));
    }
}
