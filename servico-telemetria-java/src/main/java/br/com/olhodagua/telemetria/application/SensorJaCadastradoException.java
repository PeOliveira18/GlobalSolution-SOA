package br.com.olhodagua.telemetria.application;

public class SensorJaCadastradoException extends RuntimeException {

    public SensorJaCadastradoException(String codigo) {
        super("Já existe um sensor com o código " + codigo + ".");
    }
}
