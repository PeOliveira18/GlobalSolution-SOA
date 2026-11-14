package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.TipoSensor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastrar um sensor")
public record SensorRequest(
        @Schema(description = "Código único do sensor (letras, números e hífen)", example = "SNS-PANTANAL-02")
        @NotBlank @Size(max = 40) @Pattern(regexp = "[A-Za-z0-9-]+", message = "deve ter só letras, números e hífen")
        String codigo,

        @Schema(description = "NIVEL (córrego, em cm) ou CHUVA (pluviômetro, em mm/h)", example = "NIVEL")
        @NotNull
        TipoSensor tipo,

        @Schema(example = "-23.4990")
        @NotNull @DecimalMin("-90") @DecimalMax("90")
        BigDecimal latitude,

        @Schema(example = "-46.4425")
        @NotNull @DecimalMin("-180") @DecimalMax("180")
        BigDecimal longitude,

        @Schema(description = "Altura do sensor até o fundo do córrego, em cm. Obrigatória para NIVEL", example = "400")
        @Positive @DecimalMax("99999.99")
        BigDecimal alturaInstalacaoCm,

        @Schema(example = "150")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteAtencao,

        @Schema(example = "220")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteAlerta,

        @Schema(example = "300")
        @NotNull @PositiveOrZero @DecimalMax("99999.99")
        BigDecimal limiteEmergencia,

        @Schema(description = "Id da área de risco no Serviço do Cidadão (1, 2 ou 3)", example = "1")
        @NotNull @Positive
        Long areaRiscoId
) {
}
