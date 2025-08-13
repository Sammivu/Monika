package com.project.monika.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;


@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI createOpenAPIConfig() {
        return new OpenAPI()
                // General API information
                .info(new Info()
                        .title("IFarmer API")
                        .version("1.0")
                        .description("API documentation for IFarmer application"))
                // Security schemes component
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )

                        // Define a request body for multipart form data
                        .addRequestBodies("multipartRequestBody",
                                new RequestBody().content(new Content().addMediaType(
                                        org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE,
                                        new MediaType().schema(new Schema<>().type("object")
                                                .addProperties("title", new Schema<>().type("string"))
                                                .addProperties("content", new Schema<>().type("string"))
                                                .addProperties("photo", new Schema<>().type("string").format("binary"))
                                        ))))
                )

                // Global security requirement
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}