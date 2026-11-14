package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    Optional<Leitura> findFirstBySensorIdOrderByRegistradaEmDescIdDesc(Long sensorId);

    Page<Leitura> findBySensorIdAndRegistradaEmBetween(Long sensorId, Instant desde, Instant ate, Pageable pageable);
}
