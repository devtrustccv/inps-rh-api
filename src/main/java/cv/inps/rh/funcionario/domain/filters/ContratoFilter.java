package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ContratoFilter {

  private IdentificadorUnico idFuncionario;
  private String tipoContrato;
  private Long vinculo;
  private Integer pageNumber;
  private Integer pageSize;
}
