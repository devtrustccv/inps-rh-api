package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FuncionarioFilter {

  private String nome;
  private Long direcao;
  private Long seccao;
  private Long tipoVinculoLaboral;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private Estado estado;
  private Integer pageNumber;
  private Integer pageSize;

}
