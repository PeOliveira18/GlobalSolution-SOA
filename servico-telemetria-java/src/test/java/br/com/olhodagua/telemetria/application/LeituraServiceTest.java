package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.application.dto.LeituraRegistradaResponse;
import br.com.olhodagua.telemetria.application.dto.LeituraRequest;
import br.com.olhodagua.telemetria.domain.ClassificadorNivel;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.SensoresDeTeste;
import br.com.olhodagua.telemetria.domain.Unidade;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.LeituraRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeituraServiceTest {

    private static final Instant AGORA = Instant.parse("2026-11-15T21:42:00Z");

    @Mock
    SensorRepository sensores;

    @Mock
    LeituraRepository leituras;

    @Mock
    EventoNivelRepository eventos;

    @Mock
    NotificadorCidadao notificador;

    LeituraService service;
    Sensor pantanal;

    @BeforeEach
    void preparar() {
        service = new LeituraService(sensores, leituras, eventos, new ClassificadorNivel(), notificador,
                Clock.fixed(AGORA, ZoneOffset.UTC));
        pantanal = SensoresDeTeste.pantanal();
        lenient().when(sensores.findByCodigo("SNS-PANTANAL-01")).thenReturn(Optional.of(pantanal));
        lenient().when(leituras.save(any())).then(returnsFirstArg());
        lenient().when(eventos.save(any())).then(returnsFirstArg());
    }

    @Test
    void abreEventoEAvisaOCidadaoQuandoAFaixaSobe() {
        semEventoAberto();

        LeituraRegistradaResponse resposta = service.registrar(leitura("160"));

        ArgumentCaptor<EventoNivel> aberto = ArgumentCaptor.forClass(EventoNivel.class);
        verify(notificador).alertaAberto(aberto.capture());
        assertThat(aberto.getValue().getFaixa()).isEqualTo(Faixa.ATENCAO);
        assertThat(aberto.getValue().getValorDisparo()).isEqualByComparingTo("160");
        assertThat(resposta.faixa()).isEqualTo(Faixa.ATENCAO);
        assertThat(resposta.eventoAberto()).isTrue();
    }

    @Test
    void naoChamaOCidadaoQuandoAFaixaSeMantem() {
        EventoNivel atual = eventoAberto(Faixa.ALERTA, "230");

        LeituraRegistradaResponse resposta = service.registrar(leitura("240"));

        verifyNoInteractions(notificador);
        verify(eventos, never()).save(any());
        assertThat(atual.isAtivo()).isTrue();
        assertThat(resposta.eventoAberto()).isTrue();
    }

    @Test
    void quandoSobeDeNovoFechaOEventoAnteriorEAbreOutro() {
        EventoNivel anterior = eventoAberto(Faixa.ATENCAO, "160");

        service.registrar(leitura("230"));

        assertThat(anterior.isAtivo()).isFalse();
        ArgumentCaptor<EventoNivel> novo = ArgumentCaptor.forClass(EventoNivel.class);
        verify(notificador).alertaAberto(novo.capture());
        assertThat(novo.getValue().getFaixa()).isEqualTo(Faixa.ALERTA);
        verify(notificador, never()).alertaEncerrado(any());
    }

    @Test
    void quandoVoltaAoNormalEncerraOEventoEAvisaOCidadao() {
        EventoNivel aberto = eventoAberto(Faixa.EMERGENCIA, "310");

        LeituraRegistradaResponse resposta = service.registrar(leitura("90"));

        assertThat(aberto.isAtivo()).isFalse();
        assertThat(aberto.getEncerradoEm()).isEqualTo(AGORA);
        verify(notificador).alertaEncerrado(aberto);
        verify(notificador, never()).alertaAberto(any());
        assertThat(resposta.faixa()).isEqualTo(Faixa.NORMAL);
        assertThat(resposta.eventoAberto()).isFalse();
    }

    @Test
    void quandoDesceSemChegarAoNormalSoAtualizaSemAvisar() {
        EventoNivel aberto = eventoAberto(Faixa.EMERGENCIA, "310");

        service.registrar(leitura("230"));

        assertThat(aberto.isAtivo()).isTrue();
        assertThat(aberto.getFaixa()).isEqualTo(Faixa.ALERTA);
        verifyNoInteractions(notificador);
    }

    @Test
    void sensorDeChuvaNaoAbreEvento() {
        Sensor chuva = SensoresDeTeste.chuvaPantanal();
        when(sensores.findByCodigo("CHV-PANTANAL-01")).thenReturn(Optional.of(chuva));

        LeituraRegistradaResponse resposta = service.registrar(
                new LeituraRequest("CHV-PANTANAL-01", new BigDecimal("45"), Unidade.MM_H));

        assertThat(resposta.faixa()).isEqualTo(Faixa.ALERTA);
        assertThat(resposta.eventoAberto()).isFalse();
        verifyNoInteractions(eventos, notificador);
    }

    @Test
    void sensorInexistenteGeraErroDeNaoEncontrado() {
        when(sensores.findByCodigo("SNS-NAO-EXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(
                new LeituraRequest("sns-nao-existe", new BigDecimal("100"), Unidade.CM)))
                .isInstanceOf(SensorNaoEncontradoException.class)
                .hasMessageContaining("Sensor não encontrado");
        verifyNoInteractions(leituras, eventos, notificador);
    }

    private LeituraRequest leitura(String valor) {
        return new LeituraRequest("SNS-PANTANAL-01", new BigDecimal(valor), Unidade.CM);
    }

    private void semEventoAberto() {
        when(eventos.findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(any())).thenReturn(Optional.empty());
    }

    private EventoNivel eventoAberto(Faixa faixa, String valorDisparo) {
        EventoNivel evento = new EventoNivel(pantanal, faixa, new BigDecimal(valorDisparo), AGORA.minusSeconds(600));
        when(eventos.findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(any())).thenReturn(Optional.of(evento));
        return evento;
    }
}
