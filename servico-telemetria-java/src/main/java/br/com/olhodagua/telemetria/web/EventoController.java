package br.com.olhodagua.telemetria.web;

import br.com.olhodagua.telemetria.application.EventoService;
import br.com.olhodagua.telemetria.application.dto.EventoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Eventos", description = "Períodos em que um córrego ficou acima da faixa NORMAL")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @Operation(summary = "Lista os eventos de nível, do mais recente para o mais antigo")
    @ApiResponse(responseCode = "200", description = "Lista de eventos")
    @ApiResponse(responseCode = "400", description = "Parâmetro com formato inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public List<EventoResponse> listar(
            @Parameter(description = "true para listar só os eventos em andamento", example = "true")
            @RequestParam(defaultValue = "false") boolean ativos,
            @Parameter(description = "Filtra pela área de risco", example = "1")
            @RequestParam(required = false) Long areaRiscoId) {
        return eventoService.listar(ativos, areaRiscoId);
    }
}
