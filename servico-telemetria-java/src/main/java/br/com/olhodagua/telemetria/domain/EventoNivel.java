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
@Table(name = "OT_EVENTO_NIVEL")
public class EventoNivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENSOR_ID", nullable = false)
    private Sensor sensor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Faixa faixa;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal valorDisparo;

    @Column(nullable = false)
    private Instant iniciadoEm;

    private Instant encerradoEm;

    private Long alertaCidadaoId;

    @Column(nullable = false)
    private boolean notificado;

    protected EventoNivel() {
    }

    public EventoNivel(Sensor sensor, Faixa faixa, BigDecimal valorDisparo, Instant iniciadoEm) {
        if (faixa == null || faixa == Faixa.NORMAL) {
            throw new RegraVioladaException("Um evento de nível só existe acima da faixa NORMAL.");
        }
        this.sensor = sensor;
        this.faixa = faixa;
        this.valorDisparo = valorDisparo;
        this.iniciadoEm = iniciadoEm;
        this.notificado = false;
    }

    public boolean isAtivo() {
        return encerradoEm == null;
    }

    public void encerrar(Instant instante) {
        if (!isAtivo()) {
            throw new RegraVioladaException("O evento já foi encerrado.");
        }
        if (instante.isBefore(iniciadoEm)) {
            throw new RegraVioladaException("O evento não pode terminar antes de começar.");
        }
        this.encerradoEm = instante;
    }

    public void rebaixarPara(Faixa novaFaixa) {
        if (novaFaixa == null || novaFaixa == Faixa.NORMAL || novaFaixa.acimaDe(faixa)) {
            throw new RegraVioladaException("Um evento só pode descer para uma faixa entre ATENCAO e a faixa atual.");
        }
        this.faixa = novaFaixa;
    }

    public Long getId() {
        return id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Faixa getFaixa() {
        return faixa;
    }

    public BigDecimal getValorDisparo() {
        return valorDisparo;
    }

    public Instant getIniciadoEm() {
        return iniciadoEm;
    }

    public Instant getEncerradoEm() {
        return encerradoEm;
    }

    public Long getAlertaCidadaoId() {
        return alertaCidadaoId;
    }

    public boolean isNotificado() {
        return notificado;
    }
}
