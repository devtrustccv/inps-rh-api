package cv.inps.rh.funcionario.domain.filters;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ContratoFilter {

  private Long vinculo;
  private Integer pageNumber;
  private Integer pageSize;
}
