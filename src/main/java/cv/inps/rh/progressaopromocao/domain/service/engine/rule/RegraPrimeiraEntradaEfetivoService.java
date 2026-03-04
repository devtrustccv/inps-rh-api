package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegraPrimeiraEntradaEfetivoService {

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;

  public boolean atingiuTempoPrimeiraProgressao(CarreiraEntity carreira) {

    if (!isPrimeiraProgressao(carreira)) {
      return true; // não é primeira progressão
    }

    LocalDate inicio = carreira.getDataInicio();

    if (inicio == null) return false;

    return inicio.plusYears(6)
        .isBefore(LocalDate.now());
  }

  private boolean isPrimeiraProgressao(CarreiraEntity carreira) {

    var evolucoes =
        evolucaoRepository.findByCarreiraIdDeId(
            carreira.getId()
        );

    return evolucoes.isEmpty();
  }
}
