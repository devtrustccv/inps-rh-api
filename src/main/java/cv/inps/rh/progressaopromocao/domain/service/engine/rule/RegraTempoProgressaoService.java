package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import org.springframework.stereotype.Service;

@Service
public class RegraTempoProgressaoService {

  private static final int TEMPO_MINIMO_COMISSAO_SERVICO = 4;
  private static final int TEMPO_MINIMO_CARREIRA_NORMAL = 3;
  private static final int TEMPO_MINIMO_CARREIRA_BASE = 2;

  public int determinarTempoMinimoProgressao(CarreiraEntity career) {

    if (career.getCargoId() != null) {
      return TEMPO_MINIMO_COMISSAO_SERVICO;

      // todo add new field to carreira to know type

      // TODO 05/03/2026 17:37 o funcionario tem uma carreira base, um funcionario que é chefe tem uma carreira base obrigatoriamente

      // return TEMPO_MINIMO_CARREIRA_BASE;
    }

    return TEMPO_MINIMO_CARREIRA_NORMAL;
  }
}
