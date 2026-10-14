package com.campusbridge.placement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI campusBridgeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusBridge API")
                        .description("Naan Mudhalvan Inspired Virtual Placement Drive Dashboard with Multi-Company Interview Lifecycle Coordination")
                        .version("1.0.0")
                        .contact(new Contact().name("CampusBridge Team").email("contact@campusbridge.edu"))
                        .license(new License().name("Educational Use").url("https://campusbridge.edu")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .schemaRequirement("Bearer Authentication", new SecurityScheme()
                        .name("Bearer Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
