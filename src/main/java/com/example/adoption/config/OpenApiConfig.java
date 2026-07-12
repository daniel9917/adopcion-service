package com.example.adoption.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adoptionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Adoption Service API")
                        .description("API for managing pets, pictures, and adoption applications.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Adoption Service Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project docs")
                        .url("https://example.com/docs"));
    }
}
