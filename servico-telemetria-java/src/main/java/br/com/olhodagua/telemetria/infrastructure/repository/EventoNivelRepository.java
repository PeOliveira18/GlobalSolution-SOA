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
            select e from EventoNivel e join fetch e.sensor
            where e.encerradoEm is null and e.notificado = false
            order by e.iniciadoEm
            """)
    List<EventoNivel> aberturasPendentes();

    @Query("""
            select e from EventoNivel e
            where e.sensor.id = :sensorId
              and e.encerradoEm is not null
              and e.alertaCidadaoId is not null
              and e.encerramentoNotificado = false
            order by e.iniciadoEm
            """)
    List<EventoNivel> encerramentosPendentes(@Param("sensorId") Long sensorId);

    @Query("""
            select e from EventoNivel e join fetch e.sensor s
            where e.encerradoEm is not null
              and e.alertaCidadaoId is not null
              and e.encerramentoNotificado = false
              and not exists (select a.id from EventoNivel a where a.sensor = s and a.encerradoEm is null)
            order by e.iniciadoEm
            """)
    List<EventoNivel> encerramentosPendentesDeSensoresNormais();

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
