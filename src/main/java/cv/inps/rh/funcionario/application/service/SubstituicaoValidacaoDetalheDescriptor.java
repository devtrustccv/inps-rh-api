package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para a SUBSTITUIÇÃO. Ver
 * {@link MobilidadeValidacaoDetalheDescriptor} para o padrão. Validação INSERT, referenciaId =
 * substituicao.id (casa com a instância auditada).
 */
@Component
public class SubstituicaoValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.SUBSTITUICAO.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "SubstituicaoEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("motivo", "obs", "dataInicio", "dataFim", "substituidoTiprelId");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "motivo", "Motivo",
        "obs", "Observações",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "substituidoTiprelId", "Colaborador substituído");
  }
}
