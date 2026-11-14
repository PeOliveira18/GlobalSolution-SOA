package br.com.olhodagua.telemetria.domain;

public enum Faixa {
    NORMAL, ATENCAO, ALERTA, EMERGENCIA;

    public boolean acimaDe(Faixa outra) {
        return compareTo(outra) > 0;
    }
}
