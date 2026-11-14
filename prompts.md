# Registro de uso de IA

Conforme os combinados da GS: a IA foi usada como copiloto, e a equipe sabe explicar cada linha.

| Data | Ferramenta | Para quê | Prompt (resumo) |
|---|---|---|---|
| 08/11/2026 | Claude Code | Analisar o briefing da GS e escolher o nicho | "Analise o PDF da GS CitizenTech e disserte sobre o tema para escolhermos um nicho (Mobile, SOA e C#)" |
| 08/11/2026 | Claude Code | Rascunho da Ficha, domínio e contratos entre serviços | "Alagamentos" (nicho escolhido); Java Spring Boot para SOA |
| 08/11/2026 | Claude Code | SOA · J1: esqueleto do Serviço de Telemetria (Spring Boot 4.1.2, Java 21, springdoc 3.2.0, application.yml na porta 8081, pacotes domain/application/infrastructure/web, CORS e metadados do OpenAPI) | "Vamos fazer a etapa J1 da Parte F do PLANO.md no servico-telemetria-java/, seguindo os nomes, portas e contratos da Parte B" |
| 11/11/2026 | Claude Code | SOA · J1 adaptada ao Oracle da FIAP: troca do driver do PostgreSQL por ojdbc11 e flyway-database-oracle, histórico do Flyway em OT_FLYWAY_HISTORY, credenciais só por variável de ambiente, perfil de teste com H2 em MODE=Oracle e código sem comentários | "Atualizações importantes antes de continuar o Serviço de Telemetria (SOA): banco Oracle da FIAP com prefixo OT_, integração com o Cidadão e regras do projeto" |
| 11/11/2026 | Claude Code | SOA · J2: migrações Flyway V1 a V4 em SQL do Oracle (OT_SENSOR, OT_LEITURA, OT_EVENTO_NIVEL e seed dos 4 sensores da B4), entidades JPA, repositórios Spring Data e teste das migrações no H2 | "Pode começar a J2" |
| 14/11/2026 | Claude Code | SOA · J3: regras de domínio (limites crescentes, altura de instalação, unidade por tipo, sensor ativo), ClassificadorNivel, LeituraService com a regra de eventos da C2.3, endpoints da C2.4 com DTOs record, Bean Validation, erros em ProblemDetail, documentação springdoc e testes de domínio e com Mockito | "Pode começar a J3" |
| 14/11/2026 | Claude Code | SOA · J4: integração com o Serviço do Cidadão (CidadaoRestClient com X-Api-Key para os contratos 2 e 3), reenvio agendado de alertas e encerramentos pendentes, migração V5 (encerramento notificado) e testes com Mockito, MockRestServiceServer e HTTP real | "Pode começar a J4" |
| 14/11/2026 | Claude Code | SOA · J5: README com integrantes, instruções de execução, migrações, endpoints, integração, tabela rubrica → código e evidências; prints gerados a partir de saídas reais (Swagger, Flyway no Oracle, mvn test e API); .env.example; teste de integração da API com MockMvc | "Pode começar a J5" |
| 14/11/2026 | Claude Code | SOA · PLANO.md atualizado para o Oracle da FIAP (B1 a B3, C2, C6, Etapa 0 e etapas CS1, CS3, J1, J2 e J4), partindo da cópia mestre do repositório Olho-dagua | "atualize o PLANO.md para Oracle" |
