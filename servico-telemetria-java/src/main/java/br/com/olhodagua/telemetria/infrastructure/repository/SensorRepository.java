package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByCodigo(String codigo);
}
