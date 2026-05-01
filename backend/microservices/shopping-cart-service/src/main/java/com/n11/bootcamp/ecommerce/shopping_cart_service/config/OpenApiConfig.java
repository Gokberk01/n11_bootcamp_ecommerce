package com.n11.bootcamp.ecommerce.shopping_cart_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shopping Card Service API")
                        .version("1.0.0")
                        .description("API documentation for the Shopping Cart Microservice, including operations for managing user carts, adding products, and calculating total prices."));
    }
}
