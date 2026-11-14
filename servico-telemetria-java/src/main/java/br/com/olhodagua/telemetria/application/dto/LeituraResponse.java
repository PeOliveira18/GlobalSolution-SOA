package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.application.FusoHorario;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Leitura;
import br.com.olhodagua.telemetria.domain.Unidade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Leitura do histórico de um sensor")
public record LeituraResponse(
        @Schema(example = "991")
        Long id,

        @Schema(example = "235.00")
        BigDecimal valor,

        @Schema(example = "CM")
        Unidade unidade,

        @Schema(example = "ALERTA")
        Faixa faixa,

        @Schema(example = "2026-11-15T18:42:00-03:00")
        OffsetDateTime registradaEm
) {

    public static LeituraResponse de(Leitura leitura) {
        return new LeituraResponse(
                leitura.getId(),
                leitura.getValor(),
                leitura.getUnidade(),
                leitura.getFaixaCalculada(),
                FusoHorario.emSaoPaulo(leitura.getRegistradaEm()));
    }
}
