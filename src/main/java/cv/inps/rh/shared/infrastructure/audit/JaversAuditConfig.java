package cv.inps.rh.shared.infrastructure.audit;

import cv.inps.rh.shared.application.services.AuthenticatedUserHelper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCategoriaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.javers.core.Javers;
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJpaJaversBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fiação manual do JaVers.
 *
 * <p>Substitui o auto-config {@code JaversSqlAutoConfiguration} (excluído em
 * {@code application.properties}) por um motivo concreto: o auto-config detecta o dialecto a partir
 * do Hibernate e o {@code DialectMapper} do JaVers só reconhece {@code org.hibernate.dialect.OracleDialect}
 * via {@code instanceof}. Este projeto usa {@code OracleLegacyDialect} (community) — que não herda
 * daquele — pelo que o JaVers rebentava no arranque com {@code UNSUPPORTED_SQL_DIALECT}. Aqui
 * forçamos {@link DialectName#ORACLE} directamente, sem tocar no dialecto do Hibernate da aplicação
 * (que é legacy de propósito).
 *
 * <p>Replica exatamente o que o auto-config faria: repositório SQL (com criação automática das
 * tabelas JV_*), Javers transacional, e os dois aspectos de auto-audit. Os providers de autor e de
 * propriedades de commit ligam o commit ao utilizador logado e à validação em curso.
 */
@Configuration
@EnableAspectJAutoProxy
public class JaversAuditConfig {

  /** Chaves das propriedades de commit — usadas na escrita e na consulta da grelha de detalhe. */
  public static final String PROP_VALIDACAO_UUID = "validacaoUuid";
  public static final String PROP_VALIDACAO_ID = "validacaoId";
  public static final String PROP_TABELA = "tabela";

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  ConnectionProvider jpaConnectionProvider() {
    return new JpaHibernateConnectionProvider(entityManager);
  }

  @Bean
  JaversSqlRepository javersSqlRepository(ConnectionProvider connectionProvider) {
    return SqlRepositoryBuilder
        .sqlRepository()
        .withConnectionProvider(connectionProvider)
        .withDialect(DialectName.ORACLE)
        // Schema-management DESLIGADO de propósito. O inspector do polyjdbc verifica a existência das
        // tabelas via DatabaseMetaData.getTables com o nome em minúsculas ('jv_global_id'); o Oracle
        // guarda identificadores não-citados em MAIÚSCULAS, pelo que a verificação nunca acerta e o
        // JaVers volta a emitir CREATE TABLE em cada arranque → ORA-00955 ("name already used").
        // As tabelas JV_* são criadas uma vez (db/migration/V3__javers_schema.sql) e daí em diante
        // geridas fora do JaVers.
        .withSchemaManagementEnabled(false)
        .build();
  }

  /**
   * Entidades de REFERÊNCIA tratadas como <em>Shallow Reference</em>: o JaVers regista apenas o id da
   * FK, sem fotografar o objeto referenciado nem percorrer o seu grafo. Sem isto, ao auditar a
   * Mobilidade/Carreira o JaVers arrastava Contrato, Funcionário e params INTEIROS (grafo profundo) —
   * o diff chegava a 20–50 s por commit. Marcando-os como rasos, o commit fotografa só o agregado
   * auditado e resolve-se em milissegundos. Não perdemos nada: a grelha usa apenas o id da FK; o nome
   * é resolvido na leitura pelo {@code ReferenciaNomeResolver}.
   *
   * <p>NÃO incluir aqui os agregados auditados (Mobilidade, Carreira) — ficariam comparados só por id,
   * sem diff dos próprios campos.
   */
  private static final List<Class<?>> REFERENCIAS_RASAS = List.of(
      FuncionarioEntity.class, ContratoEntity.class,
      SecaoEntity.class, ParamLocalTrabEntity.class, DirecaoEntity.class,
      ParamCargoEntity.class, ParamEscalaoEntity.class, ParamCategoriaEntity.class, ParamCarreiraEntity.class,
      // 2ª passagem (dossieFix): FKs das entidades agora auditadas (DadosBancarios, SituacaoLaboral,
      // Substituicao, DefinicaoRemuneracao, DefPagamento, ProcessoDisciplinar). Rasas para o commit não
      // arrastar o grafo profundo — a grelha usa só o id da FK (nome resolvido na leitura).
      BancoEntity.class, ParamSituacaoEntity.class, ParamSituacaoDetalheEntity.class,
      TiposRelacionamentoEntity.class, TipoMovimentoEntity.class);

  @Bean
  Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
    var builder = TransactionalJpaJaversBuilder
        .javers()
        .withTxManager(transactionManager)
        .registerJaversRepository(sqlRepository)
        .withObjectAccessHook(new HibernateUnproxyObjectAccessHook<>());
    REFERENCIAS_RASAS.forEach(tipo ->
        builder.registerEntity(EntityDefinitionBuilder.entityDefinition(tipo).withShallowReference().build()));
    return builder.build();
  }

  @Bean
  JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider,
      CommitPropertiesProvider commitPropertiesProvider) {
    return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
  }

  @Bean
  JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers,
      AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
    return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
  }

  /**
   * Autor do commit = utilizador logado (nome legível; cai para o sub e, sem sessão, para a conta
   * de sistema). Alinha com {@code USER_REGISTO_NAME} da spec.
   */
  @Bean
  AuthorProvider rhAuthorProvider(AuthenticatedUserHelper userHelper) {
    return () -> {
      String nome = userHelper.getFullName();
      return (nome != null && !nome.isBlank()) ? nome : userHelper.getSubOrSystem();
    };
  }

  /**
   * Carimba cada commit com a validação corrente (lida do {@link ValidacaoAuditContext}). Fora de
   * um fluxo de validação o holder está vazio e o commit fica sem estas propriedades — inofensivo.
   */
  @Bean
  CommitPropertiesProvider rhCommitPropertiesProvider() {
    return new CommitPropertiesProvider() {
      @Override
      public Map<String, String> provideForCommittedObject(Object domainObject) {
        var ref = ValidacaoAuditContext.current();
        if (ref == null) {
          return Map.of();
        }
        Map<String, String> props = new HashMap<>();
        if (ref.validacaoUuid() != null) {
          props.put(PROP_VALIDACAO_UUID, ref.validacaoUuid().toString());
        }
        if (ref.validacaoId() != null) {
          props.put(PROP_VALIDACAO_ID, ref.validacaoId().toString());
        }
        if (ref.tabela() != null) {
          props.put(PROP_TABELA, ref.tabela());
        }
        return props;
      }
    };
  }
}
