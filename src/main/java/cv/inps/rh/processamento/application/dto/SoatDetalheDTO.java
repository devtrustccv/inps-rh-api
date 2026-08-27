/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class SoatDetalheDTO {

  private String detalheUuid;
  private String soatUuid;
  private UUID funcionarioUuid;
  private Long remuneracaoId;
  private Long direcaoServicoId;
  private Long numeroTrabAuto;
  private Long numeroTrabManual;
  private BigDecimal valorRemunAuto;
  private BigDecimal valorRemunManual;
  private String obs;
  private String estado;

}
