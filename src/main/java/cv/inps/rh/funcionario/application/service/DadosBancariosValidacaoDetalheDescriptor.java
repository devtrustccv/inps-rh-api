package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para os DADOS BANCÁRIOS. Usa {@link #matchByTypeOnly()}
 * = true porque a validação (UPDATE/DADOS_BANCARIOS) tem {@code referenciaId} = id do FUNCIONÁRIO, não
 * o id do bancário — e os dados bancários são uma coleção. O isolamento é feito só pelo tipo, seguro
 * porque cada commit é carimbado com o seu validacaoUuid.
 */
@Component
public class DadosBancariosValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.DADOS_BANCARIOS.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "DadosBancariosEntity";
  }

  @Override
  public boolean matchByTypeOnly() {
    return true;
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("rhbId", "numConta", "nib", "dataInicio", "dataFim");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "rhbId", "Entidade bancária",
        "numConta", "Nº de conta",
        "nib", "NIB",
        "dataInicio", "Data início",
        "dataFim", "Data fim");
  }
}
