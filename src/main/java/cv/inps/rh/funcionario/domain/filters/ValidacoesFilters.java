package cv.inps.rh.funcionario.domain.filters;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ValidacoesFilters {

  private String nomeColaborador;
  private String tipoAccao;
  private String referenciaName;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private Integer pageNumber;
  private Integer pageSize;

}
