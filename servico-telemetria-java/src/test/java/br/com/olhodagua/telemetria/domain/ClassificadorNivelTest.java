package br.com.olhodagua.telemetria.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ClassificadorNivelTest {

    private final ClassificadorNivel classificador = new ClassificadorNivel();

    @ParameterizedTest(name = "{0} cm no SNS-PANTANAL-01 é {1}")
    @CsvSource({
            "0, NORMAL",
            "100, NORMAL",
            "149.99, NORMAL",
            "150, ATENCAO",
            "160, ATENCAO",
            "219.99, ATENCAO",
            "220, ALERTA",
            "235, ALERTA",
            "299.99, ALERTA",
            "300, EMERGENCIA",
            "310, EMERGENCIA",
            "400, EMERGENCIA"
    })
    void classificaONivelPelosLimitesDoSensor(String valor, Faixa esperada) {
        assertThat(classificador.classificar(SensoresDeTeste.pantanal(), new BigDecimal(valor))).isEqualTo(esperada);
    }

    @ParameterizedTest(name = "{0} mm/h no CHV-PANTANAL-01 é {1}")
    @CsvSource({
            "9.99, NORMAL",
            "10, ATENCAO",
            "29.99, ATENCAO",
            "30, ALERTA",
            "49.99, ALERTA",
            "50, EMERGENCIA"
    })
    void classificaAChuvaPelosLimitesDoPluviometro(String valor, Faixa esperada) {
        assertThat(classificador.classificar(SensoresDeTeste.chuvaPantanal(), new BigDecimal(valor))).isEqualTo(esperada);
    }
}
