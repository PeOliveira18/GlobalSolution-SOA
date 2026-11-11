package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.EventoNivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoNivelRepository extends JpaRepository<EventoNivel, Long> {

    Optional<EventoNivel> findFirstBySensorIdAndEncerradoEmIsNullOrderByIniciadoEmDesc(Long sensorId);
}
