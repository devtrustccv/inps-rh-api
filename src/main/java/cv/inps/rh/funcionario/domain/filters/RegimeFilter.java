package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegimeFilter {

  private String tipoRegime;
  private Estado estado;
  private Integer pageNumber;
  private Integer pageSize;
}
