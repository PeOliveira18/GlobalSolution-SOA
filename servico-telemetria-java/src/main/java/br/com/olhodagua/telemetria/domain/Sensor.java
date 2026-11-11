package br.com.olhodagua.telemetria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "OT_SENSOR")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoSensor tipo;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(precision = 7, scale = 2)
    private BigDecimal alturaInstalacaoCm;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal limiteAtencao;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal limiteAlerta;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal limiteEmergencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusSensor status;

    @Column(nullable = false)
    private Long areaRiscoId;

    protected Sensor() {
    }

    public Sensor(String codigo, TipoSensor tipo, BigDecimal latitude, BigDecimal longitude,
                  BigDecimal alturaInstalacaoCm, BigDecimal limiteAtencao, BigDecimal limiteAlerta,
                  BigDecimal limiteEmergencia, Long areaRiscoId) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alturaInstalacaoCm = alturaInstalacaoCm;
        this.limiteAtencao = limiteAtencao;
        this.limiteAlerta = limiteAlerta;
        this.limiteEmergencia = limiteEmergencia;
        this.areaRiscoId = areaRiscoId;
        this.status = StatusSensor.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getAlturaInstalacaoCm() {
        return alturaInstalacaoCm;
    }

    public BigDecimal getLimiteAtencao() {
        return limiteAtencao;
    }

    public BigDecimal getLimiteAlerta() {
        return limiteAlerta;
    }

    public BigDecimal getLimiteEmergencia() {
        return limiteEmergencia;
    }

    public StatusSensor getStatus() {
        return status;
    }

    public Long getAreaRiscoId() {
        return areaRiscoId;
    }
}
