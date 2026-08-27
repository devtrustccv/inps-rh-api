package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o movimento GESTÃO LABORAL ("Alterar Escalão/Cargo",
 * melhoria 2.2.1). O movimento altera o próprio tiprel (RH_T_TIPOS_RELACIONAMENTO) — para vínculos sem
 * carreira o escalão/cargo/salário vivem lá — por isso o tipo-alvo JaVers é {@code TiposRelacionamentoEntity}.
 */
@Component
public class GestaoLaboralValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.GESTAO_LABORAL.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "TiposRelacionamentoEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    // Fora, por omissão: estado (workflow), fun/contr/carreira/mob/regime/situac (estruturais),
    // est_act_adm/flg_processa (workflow), created*/lastModified*.
    return Set.of("escalaoId", "cargoId", "salario", "moeda", "tipoSituacao", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "escalaoId", "Escalão",
        "cargoId", "Cargo",
        "salario", "Salário",
        "moeda", "Moeda",
        "tipoSituacao", "Tipo de alteração",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
