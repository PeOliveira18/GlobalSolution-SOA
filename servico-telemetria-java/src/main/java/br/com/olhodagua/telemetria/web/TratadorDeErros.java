package br.com.olhodagua.telemetria.web;

import br.com.olhodagua.telemetria.application.ConsultaInvalidaException;
import br.com.olhodagua.telemetria.application.SensorJaCadastradoException;
import br.com.olhodagua.telemetria.application.SensorNaoEncontradoException;
import br.com.olhodagua.telemetria.domain.RegraVioladaException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SensorNaoEncontradoException.class)
    public ProblemDetail sensorNaoEncontrado(SensorNaoEncontradoException erro) {
        return problema(HttpStatus.NOT_FOUND, "Sensor não encontrado", erro.getMessage());
    }

    @ExceptionHandler(SensorJaCadastradoException.class)
    public ProblemDetail sensorJaCadastrado(SensorJaCadastradoException erro) {
        return problema(HttpStatus.CONFLICT, "Sensor já cadastrado", erro.getMessage());
    }

    @ExceptionHandler(RegraVioladaException.class)
    public ProblemDetail regraViolada(RegraVioladaException erro) {
        return problema(HttpStatus.BAD_REQUEST, "Regra do sensor violada", erro.getMessage());
    }

    @ExceptionHandler(ConsultaInvalidaException.class)
    public ProblemDetail consultaInvalida(ConsultaInvalidaException erro) {
        return problema(HttpStatus.BAD_REQUEST, "Dados inválidos", erro.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflitoNoBanco(DataIntegrityViolationException erro) {
        return problema(HttpStatus.CONFLICT, "Conflito com dados existentes",
                "A operação conflita com um registro que já existe.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException erro,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest requisicao) {
        Map<String, List<String>> erros = new LinkedHashMap<>();
        erro.getBindingResult().getFieldErrors().forEach(campo ->
                erros.computeIfAbsent(campo.getField(), chave -> new ArrayList<>()).add(campo.getDefaultMessage()));
        ProblemDetail corpo = problema(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos estão inválidos.");
        corpo.setProperty("errors", erros);
        return ResponseEntity.badRequest().body(corpo);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException erro,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest requisicao) {
        return ResponseEntity.badRequest().body(problema(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "O corpo da requisição está mal formatado ou tem um valor não aceito (ex.: unidade deve ser CM ou MM_H)."));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException erro, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest requisicao) {
        String nome = erro.getPropertyName() == null ? "parâmetro" : "'" + erro.getPropertyName() + "'";
        return ResponseEntity.badRequest().body(problema(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "O valor do " + nome + " não está no formato esperado."));
    }

    private static ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail corpo = ProblemDetail.forStatusAndDetail(status, detalhe);
        corpo.setTitle(titulo);
        return corpo;
    }
}
