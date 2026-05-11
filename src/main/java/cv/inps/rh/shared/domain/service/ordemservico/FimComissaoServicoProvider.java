package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FimComissaoServicoProvider implements OrdemServicoProvider {

  private final FuncionarioEntityRepository funcionarioRepository;
  private final FuncionarioRules rules;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.FIM_COMISSAO_SERVICO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = funcionarioRepository.findByUuidOrThrow(
        UUID.fromString(funcionarioId)
    );

    var currentContract = rules.getContratoComMaiorVersao(
        fun.getUuid()
    );

    return Map.of(
        "nomeColaborador", fun.getNome(),
        "cargoColaborador", currentContract.getVinculoId().getNome(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(
            currentContract.getDataInicio()
        )
    );
  }
}
