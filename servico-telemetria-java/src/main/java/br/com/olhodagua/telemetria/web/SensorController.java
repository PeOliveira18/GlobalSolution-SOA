package br.com.olhodagua.telemetria.web;

import br.com.olhodagua.telemetria.application.SensorService;
import br.com.olhodagua.telemetria.application.dto.LeituraResponse;
import br.com.olhodagua.telemetria.application.dto.PaginaResponse;
import br.com.olhodagua.telemetria.application.dto.SensorAtualizacaoRequest;
import br.com.olhodagua.telemetria.application.dto.SensorRequest;
import br.com.olhodagua.telemetria.application.dto.SensorResponse;
import br.com.olhodagua.telemetria.application.dto.StatusAtualResponse;
import br.com.olhodagua.telemetria.domain.TipoSensor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Tag(name = "Sensores", description = "Cadastro dos sensores e consulta do nível atual e do histórico")
@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Operation(summary = "Lista os sensores em funcionamento",
            description = "Não mostra os sensores desativados (INATIVO). Use areaRiscoId para o app descobrir o sensor da sua área.")
    @ApiResponse(responseCode = "200", description = "Lista de sensores")
    @ApiResponse(responseCode = "400", description = "Parâmetro com formato inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public List<SensorResponse> listar(
            @Parameter(description = "Filtra pela área de risco", example = "1")
            @RequestParam(required = false) Long areaRiscoId,
            @Parameter(description = "Filtra pelo tipo", example = "NIVEL")
            @RequestParam(required = false) TipoSensor tipo) {
        return sensorService.listar(areaRiscoId, tipo);
    }

    @Operation(summary = "Detalha um sensor")
    @ApiResponse(responseCode = "200", description = "Sensor encontrado")
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{codigo}")
    public SensorResponse buscar(
            @Parameter(description = "Código do sensor", example = "SNS-PANTANAL-01") @PathVariable String codigo) {
        return sensorService.buscar(codigo);
    }

    @Operation(summary = "Cadastra um sensor",
            description = "Os limites precisam ser crescentes. Sensor de nível precisa da altura de instalação, e o limite de emergência não pode passar dela.")
    @ApiResponse(responseCode = "201", description = "Sensor cadastrado. O header Location aponta para ele")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou limites incoerentes",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Já existe um sensor com esse código",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<SensorResponse> criar(@Valid @RequestBody SensorRequest requisicao) {
        SensorResponse criado = sensorService.criar(requisicao);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(criado.codigo())
                .toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @Operation(summary = "Atualiza os limites e o status de um sensor")
    @ApiResponse(responseCode = "200", description = "Sensor atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou limites incoerentes",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{codigo}")
    public SensorResponse atualizar(
            @Parameter(description = "Código do sensor", example = "SNS-PANTANAL-01") @PathVariable String codigo,
            @Valid @RequestBody SensorAtualizacaoRequest requisicao) {
        return sensorService.atualizar(codigo, requisicao);
    }

    @Operation(summary = "Desativa um sensor",
            description = "Exclusão lógica: o status vira INATIVO, o histórico de leituras e eventos é mantido e o sensor deixa de aceitar leituras.")
    @ApiResponse(responseCode = "204", description = "Sensor desativado")
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> desativar(
            @Parameter(description = "Código do sensor", example = "SNS-PANTANAL-01") @PathVariable String codigo) {
        sensorService.desativar(codigo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Nível atual do sensor",
            description = "Contrato ④. Devolve a última leitura. Sem nenhuma leitura ainda, a faixa vem NORMAL e o nível vem nulo.")
    @ApiResponse(responseCode = "200", description = "Situação atual")
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{codigo}/status")
    public StatusAtualResponse status(
            @Parameter(description = "Código do sensor", example = "SNS-PANTANAL-01") @PathVariable String codigo) {
        return sensorService.status(codigo);
    }

    @Operation(summary = "Histórico de leituras do sensor, da mais recente para a mais antiga",
            description = "Paginado. Sem 'desde', começa do início; sem 'ate', vai até agora. O tamanho máximo da página é 100.")
    @ApiResponse(responseCode = "200", description = "Página de leituras")
    @ApiResponse(responseCode = "400", description = "Período inválido ou parâmetro com formato errado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{codigo}/leituras")
    public PaginaResponse<LeituraResponse> leituras(
            @Parameter(description = "Código do sensor", example = "SNS-PANTANAL-01") @PathVariable String codigo,
            @Parameter(description = "Início do período (ISO 8601)", example = "2026-11-15T00:00:00-03:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @Parameter(description = "Fim do período (ISO 8601)", example = "2026-11-15T23:59:59-03:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ate,
            @Parameter(description = "Página, começando em 0", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página (1 a 100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        int pagina = Math.max(page, 0);
        int tamanho = Math.min(Math.max(size, 1), TAMANHO_MAXIMO_PAGINA);
        return sensorService.leituras(codigo, desde, ate, pagina, tamanho);
    }
}
