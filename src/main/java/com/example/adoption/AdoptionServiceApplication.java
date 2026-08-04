package com.example.adoption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AdoptionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdoptionServiceApplication.class, args);
    }
}
