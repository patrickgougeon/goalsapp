package br.com.metas.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Metas API")
                .version("1.0.0")
                .description("API REST do aplicativo de checklist e acompanhamento de metas. "
                        + "Permite cadastrar usuários, metas e itens de checklist, e gera um resultado "
                        + "personalizado de desempenho com pontuação, perfil e recomendações."));
    }
}
