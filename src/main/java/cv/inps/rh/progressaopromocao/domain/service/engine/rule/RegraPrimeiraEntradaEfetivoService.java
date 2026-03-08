package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegraPrimeiraEntradaEfetivoService {

  private static final int YEARS_FOR_FIRST_PROGRESS = 6;

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;

  public boolean atingiuTempoPrimeiraProgressao(CarreiraEntity career) {

    if (isNotPrimeiraProgressao(career.getId()))
      return true;

    return career.getDataInicio()
        .plusYears(YEARS_FOR_FIRST_PROGRESS)
        .isBefore(LocalDate.now().plusDays(1));
  }

  private boolean isNotPrimeiraProgressao(Long careerId) {
    return !evolucaoRepository.existsByCarreiraIdDeId(careerId);
  }
}
