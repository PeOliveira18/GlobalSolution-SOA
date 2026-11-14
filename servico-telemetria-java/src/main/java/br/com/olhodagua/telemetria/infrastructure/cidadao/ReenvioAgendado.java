package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.NotificadorCidadao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "cidadao.reenvio", name = "habilitado", havingValue = "true", matchIfMissing = true)
public class ReenvioAgendado {

    private final NotificadorCidadao notificador;

    public ReenvioAgendado(NotificadorCidadao notificador) {
        this.notificador = notificador;
    }

    @Scheduled(fixedDelayString = "${cidadao.reenvio.intervalo:PT1M}",
            initialDelayString = "${cidadao.reenvio.intervalo:PT1M}")
    public void reenviarPendentes() {
        notificador.reenviarPendentes();
    }
}
