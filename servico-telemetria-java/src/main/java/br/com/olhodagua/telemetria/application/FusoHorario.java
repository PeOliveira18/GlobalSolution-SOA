package br.com.olhodagua.telemetria.application;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class FusoHorario {

    public static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private FusoHorario() {
    }

    public static OffsetDateTime emSaoPaulo(Instant instante) {
        return instante == null ? null : instante.atZone(SAO_PAULO).toOffsetDateTime();
    }
}
