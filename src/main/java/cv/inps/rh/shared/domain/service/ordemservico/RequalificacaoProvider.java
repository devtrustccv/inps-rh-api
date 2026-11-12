package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequalificacaoProvider implements OrdemServicoProvider {

  private final OrdemServicoDados dados;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.REQUALIFICACAO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = dados.relacaoLaboral(funcionarioId);

    return OrdemServicoDados.vars(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargoAtual", fun.getCargoDesc(),
        "escalaoAtual", fun.getEscalaoDesc(),
        "novoCargo", "Técnico Superior",
        "novoEscalao", "10F"
    );
  }
}
