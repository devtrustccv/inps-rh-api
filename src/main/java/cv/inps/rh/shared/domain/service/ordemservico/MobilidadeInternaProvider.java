package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVRelacaoLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MobilidadeInternaProvider implements OrdemServicoProvider {

  private final RhVRelacaoLaboralEntityRepository repository;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.MOBILIDADE_INTERNA;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = repository.findByIdOrThrow(funcionarioId);

    return Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "seccao", fun.getSeccaoDesc()
    );
  }
}
