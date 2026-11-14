package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.ClienteCidadao;
import br.com.olhodagua.telemetria.application.FalhaIntegracaoCidadaoException;
import br.com.olhodagua.telemetria.application.FusoHorario;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class CidadaoRestClient implements ClienteCidadao {

    private final RestClient restClient;

    public CidadaoRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Long criarAlerta(EventoNivel evento) {
        AlertaSensorRequest corpo = new AlertaSensorRequest(
                evento.getSensor().getAreaRiscoId(),
                evento.getSensor().getCodigo(),
                evento.getValorDisparo(),
                evento.getFaixa(),
                FusoHorario.emSaoPaulo(evento.getIniciadoEm()));
        try {
            AlertaCriadoResponse resposta = restClient.post()
                    .uri("/api/alertas/sensor")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(AlertaCriadoResponse.class);
            if (resposta == null || resposta.alertaId() == null) {
                throw new FalhaIntegracaoCidadaoException("o Serviço do Cidadão não devolveu o id do alerta", null);
            }
            return resposta.alertaId();
        } catch (RestClientException erro) {
            throw new FalhaIntegracaoCidadaoException("falha ao criar alerta: " + erro.getMessage(), erro);
        }
    }

    @Override
    public void encerrarAlerta(Long alertaId) {
        try {
            restClient.patch()
                    .uri("/api/alertas/{id}/encerrar", alertaId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException erro) {
            if (erro.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw new FalhaIntegracaoCidadaoException("falha ao encerrar alerta " + alertaId + ": " + erro.getMessage(), erro);
            }
        } catch (RestClientException erro) {
            throw new FalhaIntegracaoCidadaoException("falha ao encerrar alerta " + alertaId + ": " + erro.getMessage(), erro);
        }
    }
}
