package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.Faixa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Situação atual do sensor, a partir da última leitura (contrato ④)")
public record StatusAtualResponse(
        @Schema(example = "SNS-PANTANAL-01")
        String sensorCodigo,

        @Schema(example = "1")
        Long areaRiscoId,

        @Schema(description = "Último valor medido. Nulo se o sensor ainda não enviou leitura", example = "235.0")
        BigDecimal nivelCm,

        @Schema(description = "Faixa da última leitura. NORMAL se o sensor ainda não enviou leitura", example = "ALERTA")
        Faixa faixa,

        @Schema(description = "Horário da última leitura, no fuso de São Paulo", example = "2026-11-15T18:42:00-03:00")
        OffsetDateTime atualizadoEm
) {
}
