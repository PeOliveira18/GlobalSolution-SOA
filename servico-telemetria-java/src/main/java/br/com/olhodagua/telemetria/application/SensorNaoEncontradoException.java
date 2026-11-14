package br.com.olhodagua.telemetria.application;

public class SensorNaoEncontradoException extends RuntimeException {

    public SensorNaoEncontradoException(String codigo) {
        super("Sensor não encontrado: " + codigo);
    }
}
