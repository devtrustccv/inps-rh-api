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
public class LocalTrabalhoRequestDTO  {

  @NotBlank(message = "The field <local> is required")

  private String local ;
  @NotBlank(message = "The field <pais> is required")

  private String pais ;


  private String ilha ;
  @NotBlank(message = "The field <ups> is required")

  private String ups ;


  private String estado ;

}
