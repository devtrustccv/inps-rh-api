package cv.inps.rh.funcionario.domain.filters;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CarreiraFilter {

  private String tipoCarreira;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private Integer pageNumber;
  private Integer pageSize;
}
