package br.com.olhodagua.telemetria.application;

public class ConsultaInvalidaException extends RuntimeException {

    public ConsultaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
