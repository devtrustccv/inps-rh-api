package cv.inps.rh.funcionario.application.service.remuneracao;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o RENDIMENTO (RH_T_DEF_REMUNERACOES). Validação
 * INSERT, referenciaId = remuneracao.id (casa com a instância auditada).
 */
@Component
public class RendimentoValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.RENDIMENTO.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "DefinicaoRemuneracaoEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("valor", "percentagem", "tmId", "moeda", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "valor", "Valor",
        "percentagem", "Percentagem",
        "tmId", "Tipo de movimento",
        "moeda", "Moeda",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
