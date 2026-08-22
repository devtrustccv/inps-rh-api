package cv.inps.rh.funcionario.application.service.remuneracao;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o DESCONTO/PAGAMENTO (RH_T_DEF_PAGAMENTOS).
 * Validação INSERT, referenciaId = pagamento.id (casa com a instância auditada).
 */
@Component
public class DescontoValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.DESCONTO.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "DefPagamentoEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("valor", "percentagem", "tmId", "nib", "nif", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "valor", "Valor",
        "percentagem", "Percentagem",
        "tmId", "Tipo de movimento",
        "nib", "NIB",
        "nif", "NIF",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
