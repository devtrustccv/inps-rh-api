/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class JustificarFaltaDTO  {

  
  
  private UUID colaboradorId ;
  
  
  private String nomeColaborador ;
  
  @Valid
  private List<FaltaItemDTO> itensFalta = new ArrayList<>();
  
  
  private String parecerResponsavel ;
  
  
  private Long responsavelId ;
  
  
  private String obsResponsavel ;
  
  
  private String despachoRh ;
  
  
  private Long tipoJustificacao ;
  
  
  private String validar ;
  
  
  private Integer ano ;
  
  
  private Integer mes ;

}