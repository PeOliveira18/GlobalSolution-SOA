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
import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "OT_SENSOR")
public class Sensor {

    private static final BigDecimal LATITUDE_MAXIMA = BigDecimal.valueOf(90);
    private static final BigDecimal LONGITUDE_MAXIMA = BigDecimal.valueOf(180);

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
        if (codigo == null || codigo.isBlank()) {
            throw new RegraVioladaException("O código do sensor é obrigatório.");
        }
        if (tipo == null) {
            throw new RegraVioladaException("O tipo do sensor é obrigatório.");
        }
        if (areaRiscoId == null || areaRiscoId <= 0) {
            throw new RegraVioladaException("O sensor precisa pertencer a uma área de risco válida.");
        }
        validarCoordenada(latitude, LATITUDE_MAXIMA, "latitude");
        validarCoordenada(longitude, LONGITUDE_MAXIMA, "longitude");
        this.codigo = normalizarCodigo(codigo);
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alturaInstalacaoCm = alturaInstalacaoCm;
        this.areaRiscoId = areaRiscoId;
        definirLimites(limiteAtencao, limiteAlerta, limiteEmergencia);
        this.status = StatusSensor.ATIVO;
    }

    public static String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase(Locale.ROOT);
    }

    public Leitura registrarLeitura(BigDecimal valor, Unidade unidade, ClassificadorNivel classificador, Instant instante) {
        if (status != StatusSensor.ATIVO) {
            throw new RegraVioladaException("O sensor " + codigo + " está " + status + " e não aceita leituras.");
        }
        if (unidade != tipo.getUnidade()) {
            throw new RegraVioladaException("Sensor do tipo " + tipo + " mede em " + tipo.getUnidade() + ", não em " + unidade + ".");
        }
        if (valor == null || valor.signum() < 0) {
            throw new RegraVioladaException("O valor da leitura não pode ser negativo.");
        }
        if (tipo == TipoSensor.NIVEL && valor.compareTo(alturaInstalacaoCm) > 0) {
            throw new RegraVioladaException("O nível da água não pode passar da altura de instalação do sensor ("
                    + alturaInstalacaoCm.stripTrailingZeros().toPlainString() + " cm).");
        }
        return new Leitura(this, valor, unidade, classificador.classificar(this, valor), instante);
    }

    public void atualizar(BigDecimal limiteAtencao, BigDecimal limiteAlerta, BigDecimal limiteEmergencia, StatusSensor status) {
        if (status == null) {
            throw new RegraVioladaException("O status do sensor é obrigatório.");
        }
        definirLimites(limiteAtencao, limiteAlerta, limiteEmergencia);
        this.status = status;
    }

    public void desativar() {
        this.status = StatusSensor.INATIVO;
    }

    private void definirLimites(BigDecimal atencao, BigDecimal alerta, BigDecimal emergencia) {
        if (atencao == null || alerta == null || emergencia == null) {
            throw new RegraVioladaException("Os três limites (atenção, alerta e emergência) são obrigatórios.");
        }
        if (atencao.signum() < 0) {
            throw new RegraVioladaException("Os limites não podem ser negativos.");
        }
        if (atencao.compareTo(alerta) >= 0 || alerta.compareTo(emergencia) >= 0) {
            throw new RegraVioladaException("Os limites precisam ser crescentes: atenção < alerta < emergência.");
        }
        if (tipo == TipoSensor.NIVEL) {
            if (alturaInstalacaoCm == null || alturaInstalacaoCm.signum() <= 0) {
                throw new RegraVioladaException("Sensor de nível precisa da altura de instalação em cm.");
            }
            if (emergencia.compareTo(alturaInstalacaoCm) > 0) {
                throw new RegraVioladaException("O limite de emergência não pode passar da altura de instalação do sensor.");
            }
        }
        this.limiteAtencao = atencao;
        this.limiteAlerta = alerta;
        this.limiteEmergencia = emergencia;
    }

    private static void validarCoordenada(BigDecimal valor, BigDecimal maximo, String nome) {
        if (valor == null || valor.abs().compareTo(maximo) > 0) {
            throw new RegraVioladaException("A " + nome + " precisa estar entre -" + maximo + " e " + maximo + ".");
        }
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
