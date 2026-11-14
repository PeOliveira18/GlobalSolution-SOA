package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.FalhaIntegracaoCidadaoException;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.SensoresDeTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

class CidadaoRestClientTest {

    private static final String BASE = "http://cidadao.teste";

    MockRestServiceServer servidor;
    CidadaoRestClient cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder construtor = RestClient.builder().baseUrl(BASE).defaultHeader("X-Api-Key", "chave-de-teste");
        servidor = MockRestServiceServer.bindTo(construtor).build();
        cliente = new CidadaoRestClient(construtor.build());
    }

    @Test
    void criaOAlertaNoFormatoDoContrato2() {
        EventoNivel evento = new EventoNivel(SensoresDeTeste.pantanal(), Faixa.ALERTA, new BigDecimal("235.00"),
                Instant.parse("2026-11-15T21:42:00Z"));
        servidor.expect(requestTo(BASE + "/api/alertas/sensor"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Api-Key", "chave-de-teste"))
                .andExpect(jsonPath("$.areaRiscoId").value(1))
                .andExpect(jsonPath("$.sensorCodigo").value("SNS-PANTANAL-01"))
                .andExpect(jsonPath("$.nivelCm").value(235.0))
                .andExpect(jsonPath("$.faixa").value("ALERTA"))
                .andExpect(jsonPath("$.detectadoEm").value("2026-11-15T18:42:00-03:00"))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"alertaId\":17,\"mensagem\":\"Córrego em ALERTA no Jardim Pantanal (2,35 m).\"}"));

        assertThat(cliente.criarAlerta(evento)).isEqualTo(17L);
        servidor.verify();
    }

    @Test
    void encerraOAlertaNoContrato3() {
        servidor.expect(requestTo(BASE + "/api/alertas/17/encerrar"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Api-Key", "chave-de-teste"))
                .andRespond(withNoContent());

        assertThatCode(() -> cliente.encerrarAlerta(17L)).doesNotThrowAnyException();
        servidor.verify();
    }

    @Test
    void alertaQueNaoExisteMaisNoCidadaoContaComoEncerrado() {
        servidor.expect(requestTo(BASE + "/api/alertas/99/encerrar")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatCode(() -> cliente.encerrarAlerta(99L)).doesNotThrowAnyException();
    }

    @Test
    void chaveRecusadaViraFalhaDeIntegracao() {
        servidor.expect(requestTo(BASE + "/api/alertas/17/encerrar")).andRespond(withUnauthorizedRequest());

        assertThatThrownBy(() -> cliente.encerrarAlerta(17L)).isInstanceOf(FalhaIntegracaoCidadaoException.class);
    }

    @Test
    void erroDoServidorOuRedeForaViraFalhaDeIntegracao() {
        EventoNivel evento = new EventoNivel(SensoresDeTeste.pantanal(), Faixa.ATENCAO, new BigDecimal("160"),
                Instant.parse("2026-11-15T21:42:00Z"));
        servidor.expect(requestTo(BASE + "/api/alertas/sensor")).andRespond(withServerError());
        servidor.expect(requestTo(BASE + "/api/alertas/sensor")).andRespond(withException(new IOException("Connection refused")));

        assertThatThrownBy(() -> cliente.criarAlerta(evento)).isInstanceOf(FalhaIntegracaoCidadaoException.class);
        assertThatThrownBy(() -> cliente.criarAlerta(evento)).isInstanceOf(FalhaIntegracaoCidadaoException.class);
    }
}
