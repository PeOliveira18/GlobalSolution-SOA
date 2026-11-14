package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.EventoNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventoNivelRepository extends JpaRepository<EventoNivel, Long> {

    Optional<EventoNivel> findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(Long sensorId);

    @Query("""
            select e from EventoNivel e join fetch e.sensor s
            where (:areaRiscoId is null or s.areaRiscoId = :areaRiscoId)
            order by e.iniciadoEm desc
            """)
    List<EventoNivel> listar(@Param("areaRiscoId") Long areaRiscoId);

    @Query("""
            select e from EventoNivel e join fetch e.sensor s
            where e.encerradoEm is null
              and (:areaRiscoId is null or s.areaRiscoId = :areaRiscoId)
            order by e.iniciadoEm desc
            """)
    List<EventoNivel> listarAtivos(@Param("areaRiscoId") Long areaRiscoId);
}
