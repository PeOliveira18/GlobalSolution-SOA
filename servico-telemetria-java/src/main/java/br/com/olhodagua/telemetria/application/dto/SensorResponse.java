package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.StatusSensor;
import br.com.olhodagua.telemetria.domain.TipoSensor;
import br.com.olhodagua.telemetria.domain.Unidade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Sensor cadastrado")
public record SensorResponse(
        @Schema(example = "SNS-PANTANAL-01")
        String codigo,

        @Schema(example = "NIVEL")
        TipoSensor tipo,

        @Schema(description = "Unidade das leituras deste sensor", example = "CM")
        Unidade unidade,

        @Schema(example = "1")
        Long areaRiscoId,

        @Schema(example = "-23.498500")
        BigDecimal latitude,

        @Schema(example = "-46.443000")
        BigDecimal longitude,

        @Schema(example = "400.00")
        BigDecimal alturaInstalacaoCm,

        @Schema(example = "150.00")
        BigDecimal limiteAtencao,

        @Schema(example = "220.00")
        BigDecimal limiteAlerta,

        @Schema(example = "300.00")
        BigDecimal limiteEmergencia,

        @Schema(example = "ATIVO")
        StatusSensor status
) {

    public static SensorResponse de(Sensor sensor) {
        return new SensorResponse(
                sensor.getCodigo(),
                sensor.getTipo(),
                sensor.getTipo().getUnidade(),
                sensor.getAreaRiscoId(),
                sensor.getLatitude(),
                sensor.getLongitude(),
                sensor.getAlturaInstalacaoCm(),
                sensor.getLimiteAtencao(),
                sensor.getLimiteAlerta(),
                sensor.getLimiteEmergencia(),
                sensor.getStatus());
    }
}
