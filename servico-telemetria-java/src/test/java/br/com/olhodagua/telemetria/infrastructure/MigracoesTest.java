package br.com.olhodagua.telemetria.infrastructure;

import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Leitura;
import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.StatusSensor;
import br.com.olhodagua.telemetria.domain.TipoSensor;
import br.com.olhodagua.telemetria.domain.Unidade;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.LeituraRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MigracoesTest {

    @Autowired
    SensorRepository sensores;

    @Autowired
    LeituraRepository leituras;

    @Autowired
    EventoNivelRepository eventos;

    @Test
    void seedCriaOsQuatroSensoresDaParteB4() {
        assertThat(sensores.findAll())
                .extracting(Sensor::getCodigo)
                .containsExactlyInAnyOrder("SNS-PANTANAL-01", "SNS-ROMANO-01", "SNS-IPIRANGA-01", "CHV-PANTANAL-01");
    }

    @Test
    void sensorDoPantanalTemOsLimitesDaParteB4() {
        Sensor sensor = sensores.findByCodigo("SNS-PANTANAL-01").orElseThrow();

        assertThat(sensor.getTipo()).isEqualTo(TipoSensor.NIVEL);
        assertThat(sensor.getStatus()).isEqualTo(StatusSensor.ATIVO);
        assertThat(sensor.getAreaRiscoId()).isEqualTo(1L);
        assertThat(sensor.getAlturaInstalacaoCm()).isEqualByComparingTo("400");
        assertThat(sensor.getLimiteAtencao()).isEqualByComparingTo("150");
        assertThat(sensor.getLimiteAlerta()).isEqualByComparingTo("220");
        assertThat(sensor.getLimiteEmergencia()).isEqualByComparingTo("300");
    }

    @Test
    void sensorDeChuvaNaoTemAlturaDeInstalacao() {
        Sensor chuva = sensores.findByCodigo("CHV-PANTANAL-01").orElseThrow();

        assertThat(chuva.getTipo()).isEqualTo(TipoSensor.CHUVA);
        assertThat(chuva.getAlturaInstalacaoCm()).isNull();
        assertThat(chuva.getLimiteEmergencia()).isEqualByComparingTo("50");
    }

    @Test
    void gravaLeituraEEventoLigadosAoSensor() {
        Sensor sensor = sensores.findByCodigo("SNS-ROMANO-01").orElseThrow();
        Instant agora = Instant.now().truncatedTo(ChronoUnit.MICROS);

        Leitura leitura = leituras.saveAndFlush(
                new Leitura(sensor, new BigDecimal("235.00"), Unidade.CM, Faixa.ALERTA, agora));
        EventoNivel evento = eventos.saveAndFlush(
                new EventoNivel(sensor, Faixa.ALERTA, new BigDecimal("235.00"), agora));

        assertThat(leitura.getId()).isNotNull();
        assertThat(eventos.findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(sensor.getId()))
                .get()
                .satisfies(aberto -> {
                    assertThat(aberto.getId()).isEqualTo(evento.getId());
                    assertThat(aberto.isNotificado()).isFalse();
                    assertThat(aberto.getAlertaCidadaoId()).isNull();
                });
    }
}
