package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.StatusSensor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Novos limites e status do sensor")
public record SensorAtualizacaoRequest(
        @Schema(example = "140")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteAtencao,

        @Schema(example = "210")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteAlerta,

        @Schema(example = "290")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteEmergencia,

        @Schema(description = "ATIVO, MANUTENCAO ou INATIVO", example = "ATIVO")
        @NotNull
        StatusSensor status
) {
}
