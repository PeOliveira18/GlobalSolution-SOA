package br.com.olhodagua.telemetria.application;

public class FalhaIntegracaoCidadaoException extends RuntimeException {

    public FalhaIntegracaoCidadaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
