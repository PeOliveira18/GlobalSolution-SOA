package br.com.olhodagua.telemetria.infrastructure.cidadao;

import br.com.olhodagua.telemetria.application.ClienteCidadao;
import br.com.olhodagua.telemetria.domain.EventoNivel;
import br.com.olhodagua.telemetria.domain.Faixa;
import br.com.olhodagua.telemetria.domain.SensoresDeTeste;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CidadaoConfigTest {

    HttpServer servidor;
    List<String> recebidas = new CopyOnWriteArrayList<>();
    ClienteCidadao cliente;

    @BeforeEach
    void subirCidadaoFalso() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/api/alertas", troca -> {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            recebidas.add(troca.getRequestMethod() + " " + troca.getRequestURI().getPath() + " "
                    + troca.getRequestHeaders().getFirst("X-Api-Key") + " " + corpo);
            if ("POST".equals(troca.getRequestMethod())) {
                byte[] resposta = "{\"alertaId\":17,\"mensagem\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
                troca.getResponseHeaders().add("Content-Type", "application/json");
                troca.sendResponseHeaders(201, resposta.length);
                try (OutputStream saida = troca.getResponseBody()) {
                    saida.write(resposta);
                }
            } else {
                troca.sendResponseHeaders(204, -1);
                troca.close();
            }
        });
        servidor.start();
        String endereco = "http://localhost:" + servidor.getAddress().getPort();
        cliente = new CidadaoConfig().clienteCidadao(
                new CidadaoProperties(endereco, "chave-de-teste", Duration.ofSeconds(2)));
    }

    @AfterEach
    void derrubarCidadaoFalso() {
        servidor.stop(0);
    }

    @Test
    void clienteConfiguradoFazPostEPatchDeVerdadeComAChave() {
        EventoNivel evento = new EventoNivel(SensoresDeTeste.pantanal(), Faixa.ALERTA, new BigDecimal("235.00"),
                Instant.parse("2026-11-15T21:42:00Z"));

        Long alertaId = cliente.criarAlerta(evento);
        cliente.encerrarAlerta(alertaId);

        assertThat(alertaId).isEqualTo(17L);
        assertThat(recebidas).hasSize(2);
        assertThat(recebidas.get(0))
                .startsWith("POST /api/alertas/sensor chave-de-teste ")
                .contains("\"faixa\":\"ALERTA\"", "\"detectadoEm\":\"2026-11-15T18:42:00-03:00\"");
        assertThat(recebidas.get(1)).startsWith("PATCH /api/alertas/17/encerrar chave-de-teste");
    }
}
