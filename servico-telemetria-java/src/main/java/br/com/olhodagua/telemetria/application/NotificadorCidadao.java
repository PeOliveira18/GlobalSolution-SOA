package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.domain.EventoNivel;

public interface NotificadorCidadao {

    void alertaAberto(EventoNivel evento);

    void alertaEncerrado(EventoNivel evento);
}
