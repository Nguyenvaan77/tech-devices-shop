package com.example.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tech Device Shop API")
                        .version("1.0.0")
                        .description(
                                "Comprehensive REST API for Tech Device Shop - an e-commerce platform for selling technology devices")
                        .contact(new Contact()
                                .name("Tech Device Shop Team")
                                .email("support@techdeviceshop.com")
                                .url("https://techdeviceshop.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.techdeviceshop.com")
                                .description("Production Server")));
    }
}
