package br.com.olhodagua.telemetria.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI telemetriaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Olho d'Água — Serviço de Telemetria")
                        .version("v1")
                        .description("""
                                Recebe as leituras dos sensores de nível e chuva (ESP32), classifica a faixa \
                                (NORMAL, ATENCAO, ALERTA, EMERGENCIA) e avisa o Serviço do Cidadão quando a faixa sobe. \
                                Todos os dados são simulados."""))
                .servers(List.of(new Server().url("http://localhost:8081").description("Local")));
    }
}
