package br.com.olhodagua.telemetria.web;

import br.com.olhodagua.telemetria.application.LeituraService;
import br.com.olhodagua.telemetria.application.dto.LeituraRegistradaResponse;
import br.com.olhodagua.telemetria.application.dto.LeituraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leituras", description = "Medições enviadas pelos sensores (ESP32)")
@RestController
@RequestMapping("/api/leituras")
public class LeituraController {

    private final LeituraService leituraService;

    public LeituraController(LeituraService leituraService) {
        this.leituraService = leituraService;
    }

    @Operation(
            summary = "Registra uma leitura de sensor",
            description = """
                    Contrato ①. Grava a leitura e calcula a faixa pelos limites do sensor \
                    (NORMAL, ATENCAO, ALERTA, EMERGENCIA). Em sensor de nível, quando a faixa sobe, \
                    abre um evento e avisa o Serviço do Cidadão; quando volta a NORMAL, encerra o evento. \
                    Sensor de chuva só registra a leitura.""")
    @ApiResponse(responseCode = "201", description = "Leitura registrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos, unidade errada para o tipo do sensor ou sensor inativo",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeituraRegistradaResponse registrar(@Valid @RequestBody LeituraRequest requisicao) {
        return leituraService.registrar(requisicao);
    }
}
