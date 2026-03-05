package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import org.springframework.stereotype.Service;

@Service
public class RegraTempoProgressaoService {

  public int determinarTempoMinimoProgressao(CarreiraEntity carreira) {

    if (isCargoChefia(carreira)) {
      return 4; // chefia
      //return 2; // carreira base

      // TODO 05/03/2026 17:37 verificar se o funcionario ja tem uma carreira base
      // return 2;
    }

    return 3; // carreira normal
  }

  private boolean isCargoChefia(CarreiraEntity carreira) {
    return carreira.getCargoId() != null;
  }
}
