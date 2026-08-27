/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class VinculoLaboralResponseDTO extends VinculoLaboralRequestDTO {

  @NotBlank(message = "The field <id> is required")

  private String id;

  // Espelha FLG_SALARIO (domínio TIPO_SALARIO_VINCULO) — era Integer, passou a String.
  private String salario;
  private String contratoDesc;
  private String carreiraDesc;
  private String remuneracaoDesc;
  private String tempoServicoDesc;
  private String estadoDescricao;
  private String paramContratoDesc;

}
