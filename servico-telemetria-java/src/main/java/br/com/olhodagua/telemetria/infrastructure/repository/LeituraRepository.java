package br.com.olhodagua.telemetria.infrastructure.repository;

import br.com.olhodagua.telemetria.domain.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {
}
