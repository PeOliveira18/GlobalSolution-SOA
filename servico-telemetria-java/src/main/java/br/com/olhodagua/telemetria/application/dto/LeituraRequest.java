package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.Unidade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Leitura enviada pelo sensor (contrato ①)")
public record LeituraRequest(
        @Schema(description = "Código do sensor que mediu", example = "SNS-PANTANAL-01")
        @NotBlank @Size(max = 40)
        String sensorCodigo,

        @Schema(description = "Nível da água em cm (já calculado pelo ESP32) ou chuva em mm/h", example = "235.0")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal valor,

        @Schema(description = "CM para sensor de nível, MM_H para sensor de chuva", example = "CM")
        @NotNull
        Unidade unidade
) {
}
