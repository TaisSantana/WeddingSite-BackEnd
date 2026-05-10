package br.com.wedding_site_backend.configuration;

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
    public OpenAPI weddingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wedding API — Taís & Gabriel")
                        .description("API oficial do casamento Taís & Gabriel 💜")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Taís Santana")
                                .email("taissantana.dev@gmail.com"))
                        .license(new License()
                                .name("Private API")))
                .externalDocs(new ExternalDocumentation()
                        .description("Wedding Site"));
    }
}