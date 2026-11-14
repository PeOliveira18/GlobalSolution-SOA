package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.SensoresDeTeste;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificadorCidadaoTest {

    private static final Instant INICIO = Instant.parse("2026-11-15T21:42:00Z");

    @Mock
    ClienteCidadao cliente;

    @Mock
    EventoNivelRepository eventos;

    NotificadorCidadao notificador;
    Sensor pantanal;

    @BeforeEach
    void preparar() {
        notificador = new NotificadorCidadao(cliente, eventos);
        pantanal = SensoresDeTeste.pantanal();
    }

    @Test
    void guardaOIdDoAlertaQuandoOCidadaoResponde() {
        EventoNivel evento = evento(Faixa.ALERTA);
        EventoNivel anterior = eventoEncerradoComAlerta(10L);
        when(cliente.criarAlerta(evento)).thenReturn(17L);
        when(eventos.encerramentosPendentes(any())).thenReturn(List.of(anterior));

        notificador.alertaAberto(evento);

        assertThat(evento.getAlertaCidadaoId()).isEqualTo(17L);
        assertThat(evento.isNotificado()).isTrue();
        assertThat(anterior.isEncerramentoNotificado()).isTrue();
        verify(cliente, never()).encerrarAlerta(any());
    }

    @Test
    void cidadaoForaDoArDeixaOAlertaPendenteSemFalharALeitura() {
        EventoNivel evento = evento(Faixa.ALERTA);
        when(cliente.criarAlerta(evento)).thenThrow(new FalhaIntegracaoCidadaoException("Connection refused", null));

        assertThatCode(() -> notificador.alertaAberto(evento)).doesNotThrowAnyException();

        assertThat(evento.isNotificado()).isFalse();
        assertThat(evento.getAlertaCidadaoId()).isNull();
        verify(eventos, never()).encerramentosPendentes(any());
    }

    @Test
    void voltaAoNormalEncerraOAlertaNoCidadao() {
        EventoNivel evento = eventoEncerradoComAlerta(17L);
        when(eventos.encerramentosPendentes(any())).thenReturn(List.of(evento));

        notificador.alertaEncerrado(evento);

        verify(cliente).encerrarAlerta(17L);
        assertThat(evento.isEncerramentoNotificado()).isTrue();
    }

    @Test
    void encerramentoComCidadaoForaDoArFicaPendente() {
        EventoNivel evento = eventoEncerradoComAlerta(17L);
        when(eventos.encerramentosPendentes(any())).thenReturn(List.of(evento));
        doThrow(new FalhaIntegracaoCidadaoException("timeout", null)).when(cliente).encerrarAlerta(17L);

        assertThatCode(() -> notificador.alertaEncerrado(evento)).doesNotThrowAnyException();

        assertThat(evento.isEncerramentoNotificado()).isFalse();
    }

    @Test
    void reenvioMandaOsAlertasEEncerramentosQueFicaramPendentes() {
        EventoNivel aberturaPendente = evento(Faixa.EMERGENCIA);
        EventoNivel encerramentoPendente = eventoEncerradoComAlerta(9L);
        when(eventos.aberturasPendentes()).thenReturn(List.of(aberturaPendente));
        when(eventos.encerramentosPendentesDeSensoresNormais()).thenReturn(List.of(encerramentoPendente));
        when(cliente.criarAlerta(aberturaPendente)).thenReturn(21L);

        notificador.reenviarPendentes();

        assertThat(aberturaPendente.getAlertaCidadaoId()).isEqualTo(21L);
        verify(cliente).encerrarAlerta(9L);
        assertThat(encerramentoPendente.isEncerramentoNotificado()).isTrue();
    }

    @Test
    void reenvioSemPendentesNaoChamaOCidadao() {
        when(eventos.aberturasPendentes()).thenReturn(List.of());
        when(eventos.encerramentosPendentesDeSensoresNormais()).thenReturn(List.of());

        notificador.reenviarPendentes();

        verify(cliente, never()).criarAlerta(any());
        verify(cliente, never()).encerrarAlerta(any());
    }

    private EventoNivel evento(Faixa faixa) {
        return new EventoNivel(pantanal, faixa, new BigDecimal("235"), INICIO);
    }

    private EventoNivel eventoEncerradoComAlerta(Long alertaId) {
        EventoNivel evento = new EventoNivel(pantanal, Faixa.ATENCAO, new BigDecimal("160"), INICIO.minusSeconds(600));
        evento.registrarAlertaCidadao(alertaId);
        evento.encerrar(INICIO);
        return evento;
    }
}
