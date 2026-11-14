package br.com.olhodagua.telemetria.infrastructure.cidadao;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cidadao")
public record CidadaoProperties(String baseUrl, String apiKey, Duration timeout) {
}
