/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class HoraExtraReqDTO  {

  
  
  private EstadoValidacao validar ;

  @Valid
  private List<HoraExtraDTO> horaExtra = new ArrayList<>();

  private String tipoOrdemServico ;

}