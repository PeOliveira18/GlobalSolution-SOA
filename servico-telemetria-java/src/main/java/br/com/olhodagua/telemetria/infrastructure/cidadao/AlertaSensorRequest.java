package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.domain.Faixa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AlertaSensorRequest(
        Long areaRiscoId,
        String sensorCodigo,
        BigDecimal nivelCm,
        Faixa faixa,
        OffsetDateTime detectadoEm
) {
}
