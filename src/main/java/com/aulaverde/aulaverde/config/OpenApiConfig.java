package com.aulaverde.aulaverde.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configurarOpenAPI() {

        return new OpenAPI()

                // Información de la API
                .info(new Info()
                        .title("AulaVerde API")
                        .version("1.0")
                        .description(
                            "API REST para la gestión académica de AulaVerde"))

                // Configurar autenticación JWT
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))

                // Aplicar autenticación en Swagger
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth"));
    }
}