package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProgressaoCargoProvider implements OrdemServicoProvider {

  private final OrdemServicoDados dados;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.PROGRESSAO_CARGO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = dados.relacaoLaboral(funcionarioId);

    return OrdemServicoDados.vars(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalaoAtual", fun.getEscalaoDesc(),
        "novoEscalao", "14D",
        "dataEfeito", formatNow()
    );
  }

  private String formatNow() {
    return DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now());
  }
}
