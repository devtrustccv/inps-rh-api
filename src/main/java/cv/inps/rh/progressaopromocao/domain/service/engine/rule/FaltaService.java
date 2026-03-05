package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FaltaService {

  private final FaltaEntityRepository faltaRepository;

  public boolean valida(CarreiraEntity carreira) {

    var funId = carreira.getContrVinculoId()
        .getFunId()
        .getId();

    int anoAtual = LocalDate.now().getYear();

    for (int i = 1; i <= 2; i++) {
      var faltas = faltaRepository.countFaltasPorAno(funId, anoAtual - i);
      if (faltas > 6)
        return false;
    }

    return true;
  }
}
