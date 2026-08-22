package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para a SITUAÇÃO LABORAL (ESTADO_COLABORADOR). Ver
 * {@link MobilidadeValidacaoDetalheDescriptor} para o padrão. A validação é UPDATE e o referenciaId é
 * nulo → o filtro cai para só-por-tipo (todas as alterações da SituacaoLaboralEntity desta validação).
 */
@Component
public class SituacaoLaboralValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.ESTADO_COLABORADOR.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "SituacaoLaboralEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("situacaoLaboralId", "motivoSitLabId", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "situacaoLaboralId", "Situação laboral",
        "motivoSitLabId", "Motivo",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
