package br.com.olhodagua.telemetria.domain;

public enum TipoSensor {
    NIVEL(Unidade.CM, true),
    CHUVA(Unidade.MM_H, false);

    private final Unidade unidade;
    private final boolean geraEventos;

    TipoSensor(Unidade unidade, boolean geraEventos) {
        this.unidade = unidade;
        this.geraEventos = geraEventos;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public boolean geraEventos() {
        return geraEventos;
    }
}
