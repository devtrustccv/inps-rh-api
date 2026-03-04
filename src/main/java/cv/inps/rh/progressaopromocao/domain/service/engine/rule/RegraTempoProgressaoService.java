package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import org.springframework.stereotype.Service;

@Service
public class RegraTempoProgressaoService {

  public int determinarTempoMinimoProgressao(CarreiraEntity carreira) {

    if (carreira.getContrVinculoId() == null) {
      return 3;
    }

    ParamVinculoEntity vinculo = carreira.getContrVinculoId().getVinculoId();
    if (vinculo == null) {
      return 3;
    }

    String codigo = vinculo.getCodigo();

    if (isComissaoServico(codigo)) {

      if (isCargoChefia(carreira)) {
        return 4; // chefia
      }

      return 2; // carreira base
    }

    return 3; // carreira normal
  }

  private boolean isComissaoServico(String codigo) {
    if (codigo == null) return false;

    return codigo.equalsIgnoreCase("COMISSAO")
           || codigo.equalsIgnoreCase("COMISSAO_SERVICO");
  }

  private boolean isCargoChefia(CarreiraEntity carreira) {

    if (carreira.getCargoId() == null) return false;

    String codigo = carreira.getCargoId().getDirigente();

    if (codigo == null) return false;

    return codigo.contains("DIR")
           || codigo.contains("COORD")
           || codigo.contains("CHEF");
  }
}
