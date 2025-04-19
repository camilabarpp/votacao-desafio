package com.votacao.desafio.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Votação")
                        .version("1.0")
                        .description("API para gerenciamento de votações em assembleias")
                        .contact(new Contact()
                                .name("Camila Ramão Barpp")
                                .email("milabarpp5@gmail.com")));
    }
}
