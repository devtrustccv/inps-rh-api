package cv.inps.rh.funcionario.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratoHistoricoDTO {

  private Long id;
  private String uuid;
  private Integer versao;
  private String estado;
  private String estadoDesc;
  private String dataInicio;
  private String dataFim;
  private Integer duracao;
  private String obs;

}
