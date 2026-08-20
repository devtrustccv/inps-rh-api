package cv.inps.rh.shared.application.service;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.audit.JaversAuditConfig;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.InitialValueChange;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Alimenta a grelha "Detalhe de alterações" a partir do histórico do JaVers, em vez da tabela
 * {@code RH_T_VALIDACAO_DETALHE}. Devolve exatamente o mesmo {@link ValidacaoDetalheDTO}, pelo que o
 * frontend não nota diferença.
 *
 * <p>Consulta por propriedade de commit {@code validacaoUuid} (carimbada pelo
 * {@link JaversAuditConfig.CommitPropertiesProvider rhCommitPropertiesProvider}) — junta numa só
 * query todas as alterações de todas as entidades tocadas por essa validação.
 *
 * <p>Piloto: cobre {@link ValueChange} (escalares: datas, obs, estado) e {@link ReferenceChange}
 * (FKs: direção/secção/local — resolvidas de id para o nome legível em {@link #nomeDaReferencia}).
 */
@Service
@RequiredArgsConstructor
public class JaversValidacaoDetalheReadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(JaversValidacaoDetalheReadService.class);

  private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

  /**
   * Allow-list dos campos de NEGÓCIO da mobilidade — os únicos que o utilizador edita e, por isso, os
   * únicos que a grelha "Detalhe de alterações" mostra. É uma lista fechada (não uma deny-list): tudo
   * o que não estiver aqui fica de fora por omissão, pelo que campos novos da entidade nunca vazam
   * para a grelha. Isto exclui, sem os enumerar:
   * <ul>
   *   <li><b>estado</b> (P/A/I) — estado de <em>workflow</em>, não uma edição de negócio; as suas
   *       transições (A→P ao editar, P→A ao validar) são ruído nesta grelha e pertencem a uma
   *       eventual <em>status history</em> à parte;</li>
   *   <li><b>funId</b>, <b>mobId</b>, <b>validacoes</b> — estruturais/ORM (dono do registo, ligação à
   *       mobilidade anterior, back-reference da coleção); não mudam por edição do utilizador;</li>
   *   <li><b>created*</b>/<b>lastModified*</b> — carimbo de auditoria, já exposto nas colunas
   *       {@code alteradoPor}/{@code dataAlteracao} do próprio DTO.</li>
   * </ul>
   *
   * <p>TODO ao generalizar: hoje só cobre a mobilidade; ao servir carreira/contrato, mover para um
   * mapa por entidade (ou derivar do próprio DTO/manifesto iGRP), tal como {@link #ROTULOS_CAMPO}.
   */
  private static final Set<String> CAMPOS_NEGOCIO = Set.of(
      "secaoId", "localTrabId", "instidId", "tipoSituacao", "dataInicio", "dataFim", "obs");

  /**
   * Entidade-alvo do piloto. O JaVers, ao auditar o {@code save(mobilidade)}, percorre o grafo do
   * agregado e regista também as entidades vizinhas ainda sem baseline (a própria {@code Validacao}
   * criada, a Secção/Direção referenciadas…). A grelha só quer as alterações do REGISTO em validação,
   * por isso, além de ancorar na instância-alvo (ver {@link #listar}), exigimos o tipo alvo — protege
   * contra colisão de cdoId entre tipos (ex.: SecaoEntity/634 vs MobilidadeEntity/634).
   *
   * <p>TODO ao generalizar: derivar o tipo a partir de {@code RH_T_VALIDACAO.REFERENCIA_NAME} em vez
   * de o fixar aqui (este read-service passará a servir carreira, contrato, etc.).
   */
  private static final String TIPO_ALVO_SUFIXO = "MobilidadeEntity";

  /**
   * Rótulos PT para a coluna "Campo alterado" da grelha. A chave é o nome da propriedade Java da
   * entidade (o que o JaVers reporta); o valor é o rótulo que o utilizador vê. Um campo sem entrada
   * aqui cai para o próprio nome da propriedade — nunca fica em branco.
   *
   * <p>TODO ao generalizar: hoje só cobre a mobilidade; ao servir carreira/contrato, mover para um
   * mapa por entidade (ou ler de metadados do próprio DTO/manifesto iGRP).
   */
  private static final Map<String, String> ROTULOS_CAMPO = Map.of(
      "secaoId", "Secção",
      "localTrabId", "Local de trabalho",
      "instidId", "Direcção",
      "tipoSituacao", "Tipo de mobilidade",
      "dataInicio", "Data início",
      "dataFim", "Data fim",
      "obs", "Observações");

  private final Javers javers;
  private final EntityManager entityManager;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  @Transactional(readOnly = true)
  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    // Instância-alvo desta validação: a mobilidade concreta (RH_T_MOBILIDADE.ID) que está a ser
    // validada. Ancorar aqui — e não só no tipo — é o que remove o ruído inter-mobilidade: ao percorrer
    // o grafo do agregado na aprovação, o JaVers regista alterações de OUTRAS mobilidades (ex.: a
    // anterior desativada na consolidação → "Estado A→I"); essas partilham o tipo mas têm outro id.
    ValidacaoEntity validacao = validacaoEntityRepository.findByUuid(validacaoUuid).orElse(null);
    Long referenciaId = validacao == null ? null : validacao.getReferenciaId();

    // Semântica da grelha decidida pelo tipo de validação, não por acidente do JaVers:
    //  - INSERT (criação): é um evento "criado com…"; queremos TODOS os valores iniciais dos campos de
    //    negócio (incluindo escalares, que o JaVers emite como InitialValueChange).
    //  - UPDATE (edição): só diffs reais antes→depois; um eventual snapshot inicial (baseline) que
    //    escapasse não deve poluir a grelha.
    boolean criacao = validacao != null && TipoAcao.INSERT.name().equals(validacao.getTipoAccao());

    List<Change> changes = javers.findChanges(
        QueryBuilder.anyDomainObject()
            .withCommitProperty(JaversAuditConfig.PROP_VALIDACAO_UUID, validacaoUuid.toString())
            .build());

    return changes.stream()
        .filter(PropertyChange.class::isInstance)
        .map(PropertyChange.class::cast)
        // Na EDIÇÃO, exclui o InitialValueChange (snapshot inicial escalar, tudo null→valor). Na
        // CRIAÇÃO mantemo-lo: é precisamente o valor inicial de cada campo escalar (tipo, datas) que a
        // grelha do registo deve mostrar — sem isto ficavam só as FKs e a criação vinha incompleta.
        .filter(change -> criacao || !(change instanceof InitialValueChange))
        // Só a instância-alvo (a mobilidade EXATA em validação). Combina tipo + id da referência:
        // remove tanto as refs/coleções de entidades vizinhas (outro tipo) como outras mobilidades do
        // grafo (mesmo tipo, outro id) tocadas na consolidação.
        .filter(change -> isAlvo(change.getAffectedGlobalId(), referenciaId))
        // Só campos de negócio (allow-list): remove estado (workflow), funId/mobId/validacoes
        // (estruturais) e created*/lastModified* (auditoria). Tudo o que não estiver na lista fica de
        // fora por omissão.
        .filter(change -> CAMPOS_NEGOCIO.contains(change.getPropertyName()))
        .map(this::toDto)
        .toList();
  }

  /**
   * Uma alteração é da instância-alvo quando é do tipo alvo E o seu cdoId coincide com a
   * {@code referenciaId} da validação. Se a validação não tiver referenciaId (registos antigos), cai
   * para o filtro só-por-tipo — mantém o comportamento anterior em vez de esconder tudo.
   */
  private boolean isAlvo(GlobalId globalId, Long referenciaId) {
    if (!globalId.getTypeName().endsWith(TIPO_ALVO_SUFIXO)) {
      return false;
    }
    if (referenciaId == null || !(globalId instanceof InstanceId instanceId)) {
      return true;
    }
    return String.valueOf(referenciaId).equals(String.valueOf(instanceId.getCdoId()));
  }

  private ValidacaoDetalheDTO toDto(PropertyChange<?> change) {
    var dto = new ValidacaoDetalheDTO();
    dto.setCampoAlterado(ROTULOS_CAMPO.getOrDefault(change.getPropertyName(), change.getPropertyName()));
    dto.setValorAnterior(displayLeft(change));
    dto.setValorNovo(displayRight(change));

    CommitMetadata commit = change.getCommitMetadata().orElse(null);
    if (commit != null) {
      dto.setAlteradoPor(commit.getAuthor());
      dto.setDataAlteracao(commit.getCommitDate() == null ? null : commit.getCommitDate().format(DATA_HORA));
      dto.setTabelaName(commit.getProperties().get(JaversAuditConfig.PROP_TABELA));
    }
    return dto;
  }

  private String displayLeft(PropertyChange<?> change) {
    if (change instanceof ValueChange vc) return stringify(vc.getLeft());
    if (change instanceof ReferenceChange rc) return nomeDaReferencia(rc.getLeft());
    return null;
  }

  private String displayRight(PropertyChange<?> change) {
    if (change instanceof ValueChange vc) return stringify(vc.getRight());
    if (change instanceof ReferenceChange rc) return nomeDaReferencia(rc.getRight());
    return null;
  }

  /**
   * Traduz uma referência (FK) do JaVers para o nome legível. O {@link ReferenceChange} guarda a FK
   * como {@link GlobalId} (ex.: {@code ...SecaoEntity/3}); aqui carregamos a entidade pelo id e lemos o
   * seu {@code getNome()}. As entidades de referência da mobilidade (Secção, Local, Direção) expõem
   * todas {@code getNome()}, por isso a resolução é genérica por reflexão. Se algo falhar (tipo sem
   * {@code getNome}, entidade apagada), cai para o valor cru do id — nunca rebenta a grelha.
   */
  private String nomeDaReferencia(GlobalId globalId) {
    if (globalId == null) {
      return null;
    }
    if (globalId instanceof InstanceId instanceId) {
      try {
        Class<?> tipo = Class.forName(instanceId.getTypeName());
        Long id = Long.valueOf(String.valueOf(instanceId.getCdoId()));
        Object entidade = entityManager.find(tipo, id);
        if (entidade != null) {
          Object nome = tipo.getMethod("getNome").invoke(entidade);
          if (nome != null && !nome.toString().isBlank()) {
            return nome.toString();
          }
        }
      } catch (ReflectiveOperationException | NumberFormatException e) {
        LOGGER.debug("Não foi possível resolver nome da referência {}: {}", globalId.value(), e.toString());
      }
    }
    return globalId.value(); // fallback: id cru
  }

  private String stringify(Object value) {
    return value == null ? null : value.toString();
  }
}
