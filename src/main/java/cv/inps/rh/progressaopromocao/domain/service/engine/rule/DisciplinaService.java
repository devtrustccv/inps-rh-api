package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

  private final ProcessoDisciplinarEntityRepository repository;

  public boolean valida(CarreiraEntity carreira) {

    Long funId = carreira.getContrVinculoId().getFunId().getId();

    LocalDate fim = LocalDate.now();
    LocalDate inicio = fim.minusYears(2);

    Long count = repository.existeCondenacaoPeriodo(funId, inicio, fim);

    return count == 0;
  }
}
