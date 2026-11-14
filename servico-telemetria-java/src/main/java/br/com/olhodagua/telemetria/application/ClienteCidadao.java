package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.domain.EventoNivel;

public interface ClienteCidadao {

    Long criarAlerta(EventoNivel evento);

    void encerrarAlerta(Long alertaId);
}
