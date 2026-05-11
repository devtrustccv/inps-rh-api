/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ObjectivoDTO  {
  private Long paramId;

  private Integer numero;

  private String abrangencia;

  private String objectivo;

  private String kpi;

  private BigDecimal ponderacao;

  private String meta;

}
