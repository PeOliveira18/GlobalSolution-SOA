package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.Sensor;
import br.com.olhodagua.telemetria.domain.TipoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    @Query("""
            select s from Sensor s
            where s.status <> br.com.olhodagua.telemetria.domain.StatusSensor.INATIVO
              and (:areaRiscoId is null or s.areaRiscoId = :areaRiscoId)
              and (:tipo is null or s.tipo = :tipo)
            order by s.codigo
            """)
    List<Sensor> listarAtivos(@Param("areaRiscoId") Long areaRiscoId, @Param("tipo") TipoSensor tipo);
}
