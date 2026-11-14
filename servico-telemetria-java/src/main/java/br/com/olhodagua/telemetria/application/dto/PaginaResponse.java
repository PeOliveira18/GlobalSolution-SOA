package br.com.olhodagua.telemetria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Schema(description = "Uma página de resultados")
public record PaginaResponse<T>(
        List<T> itens,

        @Schema(description = "Número da página, começando em 0", example = "0")
        int pagina,

        @Schema(example = "20")
        int tamanho,

        @Schema(example = "57")
        long totalItens,

        @Schema(example = "3")
        int totalPaginas
) {

    public static <E, T> PaginaResponse<T> de(Page<E> pagina, Function<E, T> conversor) {
        return new PaginaResponse<>(
                pagina.getContent().stream().map(conversor).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }
}
