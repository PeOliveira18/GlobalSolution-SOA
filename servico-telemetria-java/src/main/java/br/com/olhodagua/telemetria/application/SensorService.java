package br.com.olhodagua.telemetria.application;

import br.com.olhodagua.telemetria.application.dto.LeituraResponse;
import br.com.olhodagua.telemetria.application.dto.PaginaResponse;
import br.com.olhodagua.telemetria.application.dto.SensorAtualizacaoRequest;
import br.com.olhodagua.telemetria.application.dto.SensorRequest;
import br.com.olhodagua.telemetria.application.dto.SensorResponse;
import br.com.olhodagua.telemetria.application.dto.StatusAtualResponse;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.TipoSensor;
import br.com.olhodagua.telemetria.infrastructure.repository.LeituraRepository;
import br.com.olhodagua.telemetria.infrastructure.repository.SensorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SensorService {

    private final SensorRepository sensores;
    private final LeituraRepository leituras;
    private final Clock relogio;

    public SensorService(SensorRepository sensores, LeituraRepository leituras, Clock relogio) {
        this.sensores = sensores;
        this.leituras = leituras;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listar(Long areaRiscoId, TipoSensor tipo) {
        return sensores.listarAtivos(areaRiscoId, tipo).stream().map(SensorResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse buscar(String codigo) {
        return SensorResponse.de(encontrar(codigo));
    }

    @Transactional
    public SensorResponse criar(SensorRequest requisicao) {
        String codigo = Sensor.normalizarCodigo(requisicao.codigo());
        if (sensores.existsByCodigo(codigo)) {
            throw new SensorJaCadastradoException(codigo);
        }
        Sensor sensor = new Sensor(codigo, requisicao.tipo(), requisicao.latitude(), requisicao.longitude(),
                requisicao.alturaInstalacaoCm(), requisicao.limiteAtencao(), requisicao.limiteAlerta(),
                requisicao.limiteEmergencia(), requisicao.areaRiscoId());
        return SensorResponse.de(sensores.save(sensor));
    }

    @Transactional
    public SensorResponse atualizar(String codigo, SensorAtualizacaoRequest requisicao) {
        Sensor sensor = encontrar(codigo);
        sensor.atualizar(requisicao.limiteAtencao(), requisicao.limiteAlerta(), requisicao.limiteEmergencia(),
                requisicao.status());
        return SensorResponse.de(sensor);
    }

    @Transactional
    public void desativar(String codigo) {
        encontrar(codigo).desativar();
    }

    @Transactional(readOnly = true)
    public StatusAtualResponse status(String codigo) {
        Sensor sensor = encontrar(codigo);
        return leituras.findFirstBySensorIdOrderByRegistradaEmDescIdDesc(sensor.getId())
                .map(ultima -> new StatusAtualResponse(sensor.getCodigo(), sensor.getAreaRiscoId(), ultima.getValor(),
                        ultima.getFaixaCalculada(), FusoHorario.emSaoPaulo(ultima.getRegistradaEm())))
                .orElseGet(() -> new StatusAtualResponse(sensor.getCodigo(), sensor.getAreaRiscoId(), null,
                        Faixa.NORMAL, null));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<LeituraResponse> leituras(String codigo, OffsetDateTime desde, OffsetDateTime ate,
                                                    int pagina, int tamanho) {
        Sensor sensor = encontrar(codigo);
        Instant inicio = desde == null ? Instant.EPOCH : desde.toInstant();
        Instant fim = ate == null ? relogio.instant() : ate.toInstant();
        if (inicio.isAfter(fim)) {
            throw new ConsultaInvalidaException("O parâmetro 'desde' precisa ser anterior ao 'ate'.");
        }
        PageRequest pedido = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "registradaEm", "id"));
        return PaginaResponse.de(
                leituras.findBySensorIdAndRegistradaEmBetween(sensor.getId(), inicio, fim, pedido),
                LeituraResponse::de);
    }

    private Sensor encontrar(String codigo) {
        return sensores.findByCodigo(Sensor.normalizarCodigo(codigo))
                .orElseThrow(() -> new SensorNaoEncontradoException(codigo));
    }
}
