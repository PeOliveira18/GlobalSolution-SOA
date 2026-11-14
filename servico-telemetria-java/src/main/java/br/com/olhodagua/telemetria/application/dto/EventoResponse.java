package br.com.olhodagua.telemetria.application.dto;

import br.com.olhodagua.telemetria.application.FusoHorario;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Evento de nível: período em que o sensor ficou acima da faixa NORMAL")
public record EventoResponse(
        @Schema(example = "12")
        Long id,

        @Schema(example = "SNS-PANTANAL-01")
        String sensorCodigo,

        @Schema(example = "1")
        Long areaRiscoId,

        @Schema(example = "ALERTA")
        Faixa faixa,

        @Schema(description = "Valor da leitura que abriu o evento", example = "235.00")
        BigDecimal valorDisparo,

        @Schema(example = "2026-11-15T18:42:00-03:00")
        OffsetDateTime iniciadoEm,

        @Schema(description = "Nulo enquanto o evento estiver ativo", example = "2026-11-15T21:10:00-03:00")
        OffsetDateTime encerradoEm,

        @Schema(description = "Id do alerta criado no Serviço do Cidadão", example = "17")
        Long alertaCidadaoId,

        @Schema(description = "Se o Serviço do Cidadão já foi avisado", example = "true")
        boolean notificado,

        @Schema(example = "true")
        boolean ativo
) {

    public static EventoResponse de(EventoNivel evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getSensor().getCodigo(),
                evento.getSensor().getAreaRiscoId(),
                evento.getFaixa(),
                evento.getValorDisparo(),
                FusoHorario.emSaoPaulo(evento.getIniciadoEm()),
                FusoHorario.emSaoPaulo(evento.getEncerradoEm()),
                evento.getAlertaCidadaoId(),
                evento.isNotificado(),
                evento.isAtivo());
    }
}
