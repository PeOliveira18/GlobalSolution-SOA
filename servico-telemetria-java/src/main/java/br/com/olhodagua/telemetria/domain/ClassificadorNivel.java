package br.com.olhodagua.telemetria.domain;

import java.math.BigDecimal;

public class ClassificadorNivel {

    public Faixa classificar(Sensor sensor, BigDecimal valor) {
        if (valor.compareTo(sensor.getLimiteEmergencia()) >= 0) {
            return Faixa.EMERGENCIA;
        }
        if (valor.compareTo(sensor.getLimiteAlerta()) >= 0) {
            return Faixa.ALERTA;
        }
        if (valor.compareTo(sensor.getLimiteAtencao()) >= 0) {
            return Faixa.ATENCAO;
        }
        return Faixa.NORMAL;
    }
}
