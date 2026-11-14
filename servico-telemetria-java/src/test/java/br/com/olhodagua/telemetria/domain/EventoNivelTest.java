package br.com.olhodagua.telemetria.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventoNivelTest {

    private static final Instant INICIO = Instant.parse("2026-11-15T21:42:00Z");

    @Test
    void naoExisteEventoNaFaixaNormal() {
        assertThatThrownBy(() -> new EventoNivel(SensoresDeTeste.pantanal(), Faixa.NORMAL, new BigDecimal("100"), INICIO))
                .isInstanceOf(RegraVioladaException.class);
    }

    @Test
    void encerrarDeixaOEventoInativo() {
        EventoNivel evento = new EventoNivel(SensoresDeTeste.pantanal(), Faixa.ALERTA, new BigDecimal("235"), INICIO);

        evento.encerrar(INICIO.plusSeconds(600));

        assertThat(evento.isAtivo()).isFalse();
        assertThatThrownBy(() -> evento.encerrar(INICIO.plusSeconds(900))).isInstanceOf(RegraVioladaException.class);
    }

    @Test
    void podeDescerDeFaixaMasNaoSubirNemIrParaNormal() {
        EventoNivel evento = new EventoNivel(SensoresDeTeste.pantanal(), Faixa.EMERGENCIA, new BigDecimal("310"), INICIO);

        evento.rebaixarPara(Faixa.ALERTA);

        assertThat(evento.getFaixa()).isEqualTo(Faixa.ALERTA);
        assertThatThrownBy(() -> evento.rebaixarPara(Faixa.EMERGENCIA)).isInstanceOf(RegraVioladaException.class);
        assertThatThrownBy(() -> evento.rebaixarPara(Faixa.NORMAL)).isInstanceOf(RegraVioladaException.class);
    }
}
