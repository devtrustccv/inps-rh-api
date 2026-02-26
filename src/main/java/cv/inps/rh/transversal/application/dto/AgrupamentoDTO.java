package cv.inps.rh.transversal.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AgrupamentoDTO {
  private String dimensao;
  private String valor;
  private Long total;
  private List<AgrupamentoDTO> subAgrupamentos = new ArrayList<>();
}
