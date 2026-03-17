/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ObservacaoGeralDTO  {

  
  
  private String observacaoGeralAvaliacao ;
  
  
  private String descPlanoDesenvolvimento ;
  
  
  private LocalDate dataInicio ;
  
  
  private String horaInicio ;
  
  
  private String HoraFim ;

}