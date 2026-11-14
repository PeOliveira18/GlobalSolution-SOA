package br.com.olhodagua.telemetria.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SensorTest {

    private static final Instant AGORA = Instant.parse("2026-11-15T21:42:00Z");
    private final ClassificadorNivel classificador = new ClassificadorNivel();

    @Test
    void normalizaOCodigoParaMaiusculas() {
        Sensor sensor = new Sensor("  sns-teste-01 ", TipoSensor.NIVEL, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("400"), new BigDecimal("150"), new BigDecimal("220"), new BigDecimal("300"), 1L);

        assertThat(sensor.getCodigo()).isEqualTo("SNS-TESTE-01");
        assertThat(sensor.getStatus()).isEqualTo(StatusSensor.ATIVO);
    }

    @Test
    void recusaLimitesForaDeOrdem() {
        assertThatThrownBy(() -> new Sensor("SNS-X", TipoSensor.NIVEL, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("400"), new BigDecimal("220"), new BigDecimal("150"), new BigDecimal("300"), 1L))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("crescentes");
    }

    @Test
    void recusaSensorDeNivelSemAlturaDeInstalacao() {
        assertThatThrownBy(() -> new Sensor("SNS-X", TipoSensor.NIVEL, BigDecimal.ZERO, BigDecimal.ZERO,
                null, new BigDecimal("150"), new BigDecimal("220"), new BigDecimal("300"), 1L))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("altura de instalação");
    }

    @Test
    void recusaLimiteDeEmergenciaAcimaDaAlturaDoSensor() {
        assertThatThrownBy(() -> new Sensor("SNS-X", TipoSensor.NIVEL, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("250"), new BigDecimal("150"), new BigDecimal("220"), new BigDecimal("300"), 1L))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("emergência");
    }

    @Test
    void registraLeituraComAFaixaCalculada() {
        Leitura leitura = SensoresDeTeste.pantanal()
                .registrarLeitura(new BigDecimal("235"), Unidade.CM, classificador, AGORA);

        assertThat(leitura.getFaixaCalculada()).isEqualTo(Faixa.ALERTA);
        assertThat(leitura.getRegistradaEm()).isEqualTo(AGORA);
    }

    @Test
    void recusaLeituraComUnidadeDeOutroTipoDeSensor() {
        assertThatThrownBy(() -> SensoresDeTeste.pantanal()
                .registrarLeitura(new BigDecimal("20"), Unidade.MM_H, classificador, AGORA))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("mede em CM");
    }

    @Test
    void recusaNivelAcimaDaAlturaDeInstalacao() {
        assertThatThrownBy(() -> SensoresDeTeste.pantanal()
                .registrarLeitura(new BigDecimal("401"), Unidade.CM, classificador, AGORA))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("400 cm");
    }

    @Test
    void sensorDesativadoNaoAceitaLeitura() {
        Sensor sensor = SensoresDeTeste.pantanal();
        sensor.desativar();

        assertThatThrownBy(() -> sensor.registrarLeitura(new BigDecimal("100"), Unidade.CM, classificador, AGORA))
                .isInstanceOf(RegraVioladaException.class)
                .hasMessageContaining("INATIVO");
    }

    @Test
    void atualizacaoTambemExigeLimitesCrescentes() {
        Sensor sensor = SensoresDeTeste.pantanal();

        assertThatThrownBy(() -> sensor.atualizar(new BigDecimal("150"), new BigDecimal("150"), new BigDecimal("300"),
                StatusSensor.ATIVO))
                .isInstanceOf(RegraVioladaException.class);
        assertThat(sensor.getLimiteAlerta()).isEqualByComparingTo("220");
    }
}
