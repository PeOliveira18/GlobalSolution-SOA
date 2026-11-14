package br.com.olhodagua.telemetria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "OT_LEITURA")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENSOR_ID", nullable = false)
    private Sensor sensor;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Unidade unidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Faixa faixaCalculada;

    @Column(nullable = false)
    private Instant registradaEm;

    protected Leitura() {
    }

    Leitura(Sensor sensor, BigDecimal valor, Unidade unidade, Faixa faixaCalculada, Instant registradaEm) {
        this.sensor = sensor;
        this.valor = valor;
        this.unidade = unidade;
        this.faixaCalculada = faixaCalculada;
        this.registradaEm = registradaEm;
    }

    public Long getId() {
        return id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public Faixa getFaixaCalculada() {
        return faixaCalculada;
    }

    public Instant getRegistradaEm() {
        return registradaEm;
    }
}
