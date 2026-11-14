package br.com.olhodagua.telemetria.web;

import br.com.olhodagua.telemetria.TesteComBancoEmMemoria;
import br.com.olhodagua.telemetria.application.ClienteCidadao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TesteComBancoEmMemoria
@AutoConfigureMockMvc
@Transactional
class LeituraApiTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ClienteCidadao cidadao;

    @Test
    void leituraAcimaDoLimiteVoltaComoAlertaEAbreEvento() throws Exception {
        when(cidadao.criarAlerta(any())).thenReturn(17L);

        mvc.perform(post("/api/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorCodigo": "SNS-ROMANO-01", "valor": 235.0, "unidade": "CM"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.leituraId").isNumber())
                .andExpect(jsonPath("$.faixa").value("ALERTA"))
                .andExpect(jsonPath("$.eventoAberto").value(true));

        verify(cidadao).criarAlerta(any());
        mvc.perform(get("/api/sensores/SNS-ROMANO-01/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorCodigo").value("SNS-ROMANO-01"))
                .andExpect(jsonPath("$.areaRiscoId").value(2))
                .andExpect(jsonPath("$.nivelCm").value(235.0))
                .andExpect(jsonPath("$.faixa").value("ALERTA"));
        mvc.perform(get("/api/eventos").param("ativos", "true").param("areaRiscoId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alertaCidadaoId").value(17))
                .andExpect(jsonPath("$[0].notificado").value(true));
    }

    @Test
    void leituraNormalNaoChamaOCidadao() throws Exception {
        mvc.perform(post("/api/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorCodigo": "SNS-IPIRANGA-01", "valor": 50, "unidade": "CM"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.faixa").value("NORMAL"))
                .andExpect(jsonPath("$.eventoAberto").value(false));

        verify(cidadao, never()).criarAlerta(any());
    }

    @Test
    void sensorInexistenteResponde404EmProblemDetail() throws Exception {
        mvc.perform(post("/api/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorCodigo": "SNS-NAO-EXISTE", "valor": 100, "unidade": "CM"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Sensor não encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Sensor não encontrado: SNS-NAO-EXISTE"));
    }

    @Test
    void dadosInvalidosRespondem400ComOsCamposComErro() throws Exception {
        mvc.perform(post("/api/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorCodigo": "", "valor": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors.sensorCodigo").isArray())
                .andExpect(jsonPath("$.errors.valor").isArray())
                .andExpect(jsonPath("$.errors.unidade").isArray());
    }

    @Test
    void unidadeErradaParaOTipoDoSensorResponde400() throws Exception {
        mvc.perform(post("/api/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorCodigo": "SNS-PANTANAL-01", "valor": 20, "unidade": "MM_H"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Regra do sensor violada"));
    }

    @Test
    void crudDoSensorCriaBuscaEDesativa() throws Exception {
        mvc.perform(post("/api/sensores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo": "sns-api-01", "tipo": "NIVEL", "latitude": -23.49, "longitude": -46.44,
                                 "alturaInstalacaoCm": 400, "limiteAtencao": 150, "limiteAlerta": 220,
                                 "limiteEmergencia": 300, "areaRiscoId": 1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/sensores/SNS-API-01"))
                .andExpect(jsonPath("$.codigo").value("SNS-API-01"))
                .andExpect(jsonPath("$.status").value("ATIVO"));

        mvc.perform(get("/api/sensores/SNS-API-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limiteAlerta").value(220));

        mvc.perform(delete("/api/sensores/SNS-API-01"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/sensores/SNS-API-01"))
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }
}
