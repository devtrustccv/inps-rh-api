/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.progressaopromocao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AnexarOrdemServicoRequestDTO  {

  @NotBlank(message = "The field <evolucaoCarreiraUuid> is required")
  
  private String evolucaoCarreiraUuid ;
  @NotBlank(message = "The field <descricao> is required")
  
  private String descricao ;
  @NotBlank(message = "The field <requerente> is required")
  
  private String requerente ;
  @NotBlank(message = "The field <numero> is required")
  
  private String numero ;
  @NotBlank(message = "The field <ordemServicoUrl> is required")
  
  private String ordemServicoUrl ;

}