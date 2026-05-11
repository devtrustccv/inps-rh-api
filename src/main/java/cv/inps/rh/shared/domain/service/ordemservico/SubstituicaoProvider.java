package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVRelacaoLaboralEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubstituicaoProvider implements OrdemServicoProvider {

  private final RhVRelacaoLaboralEntityRepository repository;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.SUBSTITUICAO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = repository.findByIdOrThrow(funcionarioId);

    return Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "motivo", "férias",
        "dataInicio", formatNow(),
        "dataFim", DateFormatter.EXTENDED_DATE_PT.format(
            LocalDate.now().plusDays(15)
        )
    );
  }

  private String formatNow() {
    return DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now());
  }
}
