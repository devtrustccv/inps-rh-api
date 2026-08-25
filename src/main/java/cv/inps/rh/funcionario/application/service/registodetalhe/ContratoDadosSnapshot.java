package cv.inps.rh.funcionario.application.service.registodetalhe;

import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import lombok.Getter;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

/**
 * Snapshot só-leitura dos campos PRÓPRIOS do contrato (RH_T_CONTRATO_VINCULO) para o "Detalhe de
 * alterações" do registo. Como o {@code ContratoEntity} é <em>ShallowReference</em> no JaVers,
 * auditamos este POJO dedicado. Guarda apenas o que é exclusivo do contrato — tipo, vínculo e duração
 * (as datas já aparecem via carreira/mobilidade/situação, para não duplicar). Tipo e vínculo guardam o
 * NOME (não o id), prontos a mostrar.
 */
@Getter
@TypeName("RegistoContratoSnapshot")
public final class ContratoDadosSnapshot {

  @Id
  private final Long id;
  private final String tipoContrato;
  private final String vinculo;
  private final Integer duracao;

  private ContratoDadosSnapshot(ContratoEntity c) {
    this.id = c.getId();
    this.tipoContrato = c.getTpContratoId() != null ? c.getTpContratoId().getNome() : null;
    this.vinculo = c.getVinculoId() != null ? c.getVinculoId().getNome() : null;
    this.duracao = c.getDuracao();
  }

  public static ContratoDadosSnapshot of(ContratoEntity contrato) {
    return contrato == null ? null : new ContratoDadosSnapshot(contrato);
  }
}
