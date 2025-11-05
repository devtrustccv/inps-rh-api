package cv.inps.rh.funcionario.domain.filters;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MobilidadeFilters {

  private String tipoMobilidade;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private Integer pageNumber;
  private Integer pageSize;
}
