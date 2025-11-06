package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PagamentoDescontoFilter {

  private Estado estado;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer pageNumber;
  private Integer pageSize;
}
