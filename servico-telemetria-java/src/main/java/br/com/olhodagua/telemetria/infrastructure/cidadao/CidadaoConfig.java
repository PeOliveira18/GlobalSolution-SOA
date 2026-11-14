package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.ClienteCidadao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(CidadaoProperties.class)
public class CidadaoConfig {

    private static final Logger log = LoggerFactory.getLogger(CidadaoConfig.class);
    private static final String CABECALHO_CHAVE = "X-Api-Key";

    @Bean
    public ClienteCidadao clienteCidadao(CidadaoProperties propriedades) {
        if (propriedades.apiKey() == null || propriedades.apiKey().isBlank()) {
            log.warn("CIDADAO_API_KEY não foi definida: o Serviço do Cidadão vai recusar os alertas com 401");
        }
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(propriedades.timeout())
                .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(http);
        fabrica.setReadTimeout(propriedades.timeout());
        RestClient restClient = RestClient.builder()
                .baseUrl(propriedades.baseUrl())
                .defaultHeader(CABECALHO_CHAVE, propriedades.apiKey() == null ? "" : propriedades.apiKey())
                .requestFactory(fabrica)
                .build();
        return new CidadaoRestClient(restClient);
    }
}
