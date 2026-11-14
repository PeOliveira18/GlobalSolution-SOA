package br.com.olhodagua.telemetria.domain;

import java.math.BigDecimal;

public final class SensoresDeTeste {

    private SensoresDeTeste() {
    }

    public static Sensor pantanal() {
        return new Sensor("SNS-PANTANAL-01", TipoSensor.NIVEL, new BigDecimal("-23.4985"), new BigDecimal("-46.4430"),
                new BigDecimal("400"), new BigDecimal("150"), new BigDecimal("220"), new BigDecimal("300"), 1L);
    }

    public static Sensor chuvaPantanal() {
        return new Sensor("CHV-PANTANAL-01", TipoSensor.CHUVA, new BigDecimal("-23.4985"), new BigDecimal("-46.4430"),
                null, new BigDecimal("10"), new BigDecimal("30"), new BigDecimal("50"), 1L);
    }
}
