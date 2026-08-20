package cv.inps.rh.shared.application.service;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.audit.JaversAuditConfig;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.annotation.PostConstruct;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * <p>Cobre {@link ValueChange} (escalares: datas, obs…) e {@link ReferenceChange} (FKs: direção/
 * secção/local — resolvidas de id para o nome legível em {@link #nomeDaReferencia}).
 *
 * <p>O que é específico de cada módulo (tipo-alvo, campos de negócio, rótulos) vive num
 * {@link ValidacaoDetalheDescriptor} por módulo, descoberto por injeção e indexado por
 * {@code REFERENCIA_NAME}. Ligar um módulo novo à grelha é adicionar um bean — este serviço não muda.
 */
@Service
@RequiredArgsConstructor
public class JaversValidacaoDetalheReadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(JaversValidacaoDetalheReadService.class);

  private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

  private final Javers javers;
  private final EntityManager entityManager;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  /** Descritores por módulo (mobilidade, carreira…), injetados pelo Spring. Ver {@link #indexar}. */
  private final List<ValidacaoDetalheDescriptor> descriptors;
  private final Map<String, ValidacaoDetalheDescriptor> registoPorReferencia = new HashMap<>();

  @PostConstruct
  void indexar() {
    for (ValidacaoDetalheDescriptor d : descriptors) {
      ValidacaoDetalheDescriptor anterior = registoPorReferencia.put(d.referenciaName(), d);
      if (anterior != null) {
        LOGGER.warn("Dois ValidacaoDetalheDescriptor para referenciaName='{}' ({} e {}); fica o último.",
            d.referenciaName(), anterior.getClass().getSimpleName(), d.getClass().getSimpleName());
      }
    }
    LOGGER.info("Grelha 'Detalhe de alterações' (JaVers) ativa para: {}", registoPorReferencia.keySet());
  }

  @Transactional(readOnly = true)
  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    ValidacaoEntity validacao = validacaoEntityRepository.findByUuid(validacaoUuid).orElse(null);
    if (validacao == null) {
      return List.of();
    }

    // Escolhe o descritor do módulo pela REFERENCIA_NAME da validação (MOBILIDADE, CARREIRA…). Sem
    // descritor registado, a grelha vem vazia — nunca rebenta e o log diz qual falta.
    ValidacaoDetalheDescriptor descriptor = registoPorReferencia.get(validacao.getReferenciaName());
    if (descriptor == null) {
      LOGGER.warn("Sem ValidacaoDetalheDescriptor para referenciaName='{}' (validacao {}); grelha vazia.",
          validacao.getReferenciaName(), validacaoUuid);
      return List.of();
    }

    // Instância-alvo desta validação: o registo concreto (REFERENCIA_ID) que está a ser validado.
    // Ancorar aqui — e não só no tipo — remove o ruído inter-registo: ao percorrer o grafo do agregado
    // na aprovação, o JaVers regista alterações de OUTRAS instâncias do mesmo tipo (ex.: a mobilidade
    // anterior desativada na consolidação → "Estado A→I"); partilham o tipo mas têm outro id.
    Long referenciaId = validacao.getReferenciaId();

    // Semântica da grelha decidida pelo tipo de validação, não por acidente do JaVers:
    //  - INSERT (criação): é um evento "criado com…"; queremos TODOS os valores iniciais dos campos de
    //    negócio (incluindo escalares, que o JaVers emite como InitialValueChange).
    //  - UPDATE (edição): só diffs reais antes→depois; um eventual snapshot inicial (baseline) que
    //    escapasse não deve poluir a grelha.
    boolean criacao = TipoAcao.INSERT.name().equals(validacao.getTipoAccao());

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
        // Só a instância-alvo (o registo EXATO em validação). Combina tipo (do descritor) + id da
        // referência: remove refs/coleções de entidades vizinhas (outro tipo) e outras instâncias do
        // mesmo tipo (mesmo tipo, outro id) tocadas na consolidação.
        .filter(change -> isAlvo(change.getAffectedGlobalId(), referenciaId, descriptor.entityTypeSuffix()))
        // Só campos de negócio (allow-list do descritor): fora estado (workflow), FKs estruturais e
        // created*/lastModified* (auditoria). Tudo o que não estiver na lista fica de fora por omissão.
        .filter(change -> descriptor.camposNegocio().contains(change.getPropertyName()))
        .map(change -> toDto(change, descriptor.rotulos()))
        .toList();
  }

  /**
   * Uma alteração é da instância-alvo quando é do {@code tipoAlvoSuffix} E o seu cdoId coincide com a
   * {@code referenciaId} da validação. Se a validação não tiver referenciaId (registos antigos), cai
   * para o filtro só-por-tipo — mantém o comportamento anterior em vez de esconder tudo.
   */
  private boolean isAlvo(GlobalId globalId, Long referenciaId, String tipoAlvoSuffix) {
    if (!globalId.getTypeName().endsWith(tipoAlvoSuffix)) {
      return false;
    }
    if (referenciaId == null || !(globalId instanceof InstanceId instanceId)) {
      return true;
    }
    return String.valueOf(referenciaId).equals(String.valueOf(instanceId.getCdoId()));
  }

  private ValidacaoDetalheDTO toDto(PropertyChange<?> change, Map<String, String> rotulos) {
    var dto = new ValidacaoDetalheDTO();
    dto.setCampoAlterado(rotulos.getOrDefault(change.getPropertyName(), change.getPropertyName()));
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
