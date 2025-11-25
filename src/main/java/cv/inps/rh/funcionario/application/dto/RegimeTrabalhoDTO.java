/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.RegimeModalidadeDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class RegimeTrabalhoDTO  {

  
  
  private EstadoValidacao validar ;
  
  
  private String tipoRegime ;
  
  
  private LocalDate dataInicio ;
  
  
  private LocalDate dataFim ;
  
  @Valid
  private List<RegimeModalidadeDTO> regimeModalidade = new ArrayList<>();

}