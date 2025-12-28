/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class TipoContratoLaboralRequestDTO  {

  @NotBlank(message = "The field <codigo> is required")

  private String codigo ;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao ;
  @NotBlank(message = "The field <natureza> is required")

  private String vinculoId;

  private String natureza ;

  private String renovavel ;


  private Integer duracao ;


  private Integer maxNumeroRenovacao ;


  private String prazo ;


  private String estado ;

}
