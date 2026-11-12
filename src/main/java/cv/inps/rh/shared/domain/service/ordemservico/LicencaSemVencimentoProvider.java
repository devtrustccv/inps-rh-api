package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LicencaSemVencimentoProvider implements OrdemServicoProvider {

  private final OrdemServicoDados dados;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.LICENCA_S_VENCIMENTO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = dados.relacaoLaboral(funcionarioId);

    return OrdemServicoDados.vars(
        "nomeColaborador", fun.getNomeColaborador(),
        // Meses totais (getMonths() ignorava os anos: 1 ano e 2 meses dava 2).
        "periodo", fun.getDataInicioSituacao() != null && fun.getDataFimSituacao() != null
            ? Period.between(fun.getDataInicioSituacao(), fun.getDataFimSituacao()).toTotalMonths()
            : null,
        "cargo", fun.getCargoDesc(),
        "categoria", fun.getEscalaoDesc(),
        "dataEfeito", formatNow()
    );
  }

  private String formatNow() {
    return DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now());
  }
}
