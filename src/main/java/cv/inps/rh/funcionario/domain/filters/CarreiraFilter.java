package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CarreiraFilter {

  private IdentificadorUnico idFuncionario;
  private String tipoCarreira;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer pageNumber;
  private Integer pageSize;
}
