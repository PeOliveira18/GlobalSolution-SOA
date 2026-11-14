package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.application.dto.EventoResponse;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoNivelRepository eventos;

    public EventoService(EventoNivelRepository eventos) {
        this.eventos = eventos;
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listar(boolean somenteAtivos, Long areaRiscoId) {
        var encontrados = somenteAtivos ? eventos.listarAtivos(areaRiscoId) : eventos.listar(areaRiscoId);
        return encontrados.stream().map(EventoResponse::de).toList();
    }
}
