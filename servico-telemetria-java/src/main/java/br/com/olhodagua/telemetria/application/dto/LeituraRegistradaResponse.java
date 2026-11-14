package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.domain.Faixa;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado do registro de uma leitura (contrato ①)")
public record LeituraRegistradaResponse(
        @Schema(description = "Identificador da leitura gravada", example = "991")
        Long leituraId,

        @Schema(description = "Faixa calculada pelos limites do sensor", example = "ALERTA")
        Faixa faixa,

        @Schema(description = "Se o sensor ficou com um evento de nível aberto depois desta leitura", example = "true")
        boolean eventoAberto
) {
}
