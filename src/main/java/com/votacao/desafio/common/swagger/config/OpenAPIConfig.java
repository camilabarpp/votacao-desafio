package com.votacao.desafio.common.swagger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API de Votação",
                version = "1.0",
                description = "API para gerenciamento de sessões de votação"
        )
)
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
