package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegraPrimeiraEntradaEfetivoService {

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;

  public boolean atingiuTempoPrimeiraProgressao(CarreiraEntity carreira) {

    if (!isPrimeiraProgressao(carreira))
      return true; // não é primeira progressão

    var inicio = carreira.getDataInicio();
    if (inicio == null)
      return false;

    return inicio.plusYears(6) // TODO 05/03/2026 17:27 remove this magic number
        .isBefore(LocalDate.now()); // TODO 05/03/2026 15:10 validate if it is Localdate.now().plusdays(1)
  }

  private boolean isPrimeiraProgressao(CarreiraEntity carreira) {

    // TODO 05/03/2026 15:11 optimize query with bollean
    var evolucoes = evolucaoRepository.findByCarreiraIdDeId(carreira.getId());

    return evolucoes.isEmpty();
  }
}
