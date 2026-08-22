package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para a RENOVAÇÃO DE CONTRATO. A alteração da renovação
 * vive no {@code ContratoHistoricoEntity} (datas/duração propostas), mas a validação tem
 * {@code referenciaId} = id do CONTRATO — por isso {@link #matchByTypeOnly()} = true (isola só pelo
 * tipo ContratoHistoricoEntity; seguro porque o commit é carimbado com o validacaoUuid da renovação).
 */
@Component
public class RenovacaoContratoValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.RENOVACAO_CONTRATO.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "ContratoHistoricoEntity";
  }

  @Override
  public boolean matchByTypeOnly() {
    return true;
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("dataInicio", "dataFim", "duracao");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "duracao", "Duração (meses)");
  }
}
