package cv.inps.rh.funcionario.domain.filters;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CarreiraFilter {

  private String tipoCarreira;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer pageNumber;
  private Integer pageSize;
}
