package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.application.dto.LeituraRegistradaResponse;
import br.com.olhodagua.telemetria.application.dto.LeituraRequest;
import br.com.olhodagua.telemetria.domain.ClassificadorNivel;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Leitura;
import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.infrastructure.repository.EventoNivelRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.LeituraRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class LeituraService {

    private final SensorRepository sensores;
    private final LeituraRepository leituras;
    private final EventoNivelRepository eventos;
    private final ClassificadorNivel classificador;
    private final NotificadorCidadao notificador;
    private final Clock relogio;

    public LeituraService(SensorRepository sensores, LeituraRepository leituras, EventoNivelRepository eventos,
                          ClassificadorNivel classificador, NotificadorCidadao notificador, Clock relogio) {
        this.sensores = sensores;
        this.leituras = leituras;
        this.eventos = eventos;
        this.classificador = classificador;
        this.notificador = notificador;
        this.relogio = relogio;
    }

    @Transactional
    public LeituraRegistradaResponse registrar(LeituraRequest requisicao) {
        Sensor sensor = sensores.findByCodigo(Sensor.normalizarCodigo(requisicao.sensorCodigo()))
                .orElseThrow(() -> new SensorNaoEncontradoException(requisicao.sensorCodigo()));
        Instant agora = relogio.instant();
        Leitura leitura = leituras.save(
                sensor.registrarLeitura(requisicao.valor(), requisicao.unidade(), classificador, agora));
        boolean eventoAberto = sensor.getTipo().geraEventos() && acompanharEvento(sensor, leitura, agora);
        return new LeituraRegistradaResponse(leitura.getId(), leitura.getFaixaCalculada(), eventoAberto);
    }

    private boolean acompanharEvento(Sensor sensor, Leitura leitura, Instant agora) {
        Optional<EventoNivel> aberto = eventos.findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(sensor.getId());
        Faixa faixaAnterior = aberto.map(EventoNivel::getFaixa).orElse(Faixa.NORMAL);
        Faixa faixaNova = leitura.getFaixaCalculada();

        if (faixaNova.acimaDe(faixaAnterior)) {
            aberto.ifPresent(evento -> evento.encerrar(agora));
            EventoNivel novo = eventos.save(new EventoNivel(sensor, faixaNova, leitura.getValor(), agora));
            notificador.alertaAberto(novo);
            return true;
        }
        if (aberto.isEmpty()) {
            return false;
        }
        EventoNivel evento = aberto.get();
        if (faixaNova == Faixa.NORMAL) {
            evento.encerrar(agora);
            notificador.alertaEncerrado(evento);
            return false;
        }
        evento.rebaixarPara(faixaNova);
        return true;
    }
}
