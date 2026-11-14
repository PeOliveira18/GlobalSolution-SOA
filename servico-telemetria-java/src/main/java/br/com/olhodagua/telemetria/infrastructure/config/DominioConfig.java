package br.com.olhodagua.telemetria.infrastructure.config;

import br.com.olhodagua.telemetria.domain.ClassificadorNivel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DominioConfig {

    @Bean
    public ClassificadorNivel classificadorNivel() {
        return new ClassificadorNivel();
    }

    @Bean
    public Clock relogio() {
        return Clock.systemUTC();
    }
}
