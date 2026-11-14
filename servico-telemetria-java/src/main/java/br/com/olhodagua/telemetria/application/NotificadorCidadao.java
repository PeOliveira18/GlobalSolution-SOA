package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificadorCidadao {

    private static final Logger log = LoggerFactory.getLogger(NotificadorCidadao.class);

    private final ClienteCidadao cliente;
    private final EventoNivelRepository eventos;

    public NotificadorCidadao(ClienteCidadao cliente, EventoNivelRepository eventos) {
        this.cliente = cliente;
        this.eventos = eventos;
    }

    public void alertaAberto(EventoNivel evento) {
        try {
            Long alertaId = cliente.criarAlerta(evento);
            evento.registrarAlertaCidadao(alertaId);
            eventos.encerramentosPendentes(evento.getSensor().getId())
                    .forEach(EventoNivel::registrarEncerramentoAvisado);
            log.info("Alerta {} criado no Serviço do Cidadão: {} no sensor {}",
                    alertaId, evento.getFaixa(), evento.getSensor().getCodigo());
        } catch (FalhaIntegracaoCidadaoException erro) {
            log.warn("Serviço do Cidadão indisponível. O alerta {} do sensor {} fica pendente e será reenviado: {}",
                    evento.getFaixa(), evento.getSensor().getCodigo(), erro.getMessage());
        }
    }

    public void alertaEncerrado(EventoNivel evento) {
        encerrarPendentes(eventos.encerramentosPendentes(evento.getSensor().getId()));
    }

    @Transactional
    public void reenviarPendentes() {
        List<EventoNivel> aberturas = eventos.aberturasPendentes();
        List<EventoNivel> encerramentos = eventos.encerramentosPendentesDeSensoresNormais();
        if (aberturas.isEmpty() && encerramentos.isEmpty()) {
            return;
        }
        log.info("Reenviando ao Serviço do Cidadão: {} alerta(s) e {} encerramento(s) pendentes",
                aberturas.size(), encerramentos.size());
        aberturas.forEach(this::alertaAberto);
        encerrarPendentes(encerramentos);
    }

    private void encerrarPendentes(List<EventoNivel> pendentes) {
        for (EventoNivel evento : pendentes) {
            try {
                cliente.encerrarAlerta(evento.getAlertaCidadaoId());
                evento.registrarEncerramentoAvisado();
                log.info("Alerta {} encerrado no Serviço do Cidadão (sensor {})",
                        evento.getAlertaCidadaoId(), evento.getSensor().getCodigo());
            } catch (FalhaIntegracaoCidadaoException erro) {
                log.warn("Serviço do Cidadão indisponível. O encerramento do alerta {} fica pendente e será reenviado: {}",
                        evento.getAlertaCidadaoId(), erro.getMessage());
            }
        }
    }
}
