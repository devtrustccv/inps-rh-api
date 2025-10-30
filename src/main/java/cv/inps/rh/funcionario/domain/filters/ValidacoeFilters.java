package cv.inps.rh.funcionario.domain.filters;

import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ValidacoeFilters {

  private String nomeColaborador;
  private String tipoAccao;
  private String referenciaName;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private Integer pageNumber;
  private Integer pageSize;

}
