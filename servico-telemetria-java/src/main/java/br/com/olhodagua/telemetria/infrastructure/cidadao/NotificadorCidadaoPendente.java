package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.NotificadorCidadao;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificadorCidadaoPendente implements NotificadorCidadao {

    private static final Logger log = LoggerFactory.getLogger(NotificadorCidadaoPendente.class);

    @Override
    public void alertaAberto(EventoNivel evento) {
        log.warn("Alerta {} do sensor {} ficou pendente: a integração com o Serviço do Cidadão ainda não está ligada",
                evento.getFaixa(), evento.getSensor().getCodigo());
    }

    @Override
    public void alertaEncerrado(EventoNivel evento) {
        log.warn("Encerramento do evento {} do sensor {} ficou pendente: a integração com o Serviço do Cidadão ainda não está ligada",
                evento.getId(), evento.getSensor().getCodigo());
    }
}
