/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AumentoRowDTO  {

  
  
  private String designacao ;
  
  
  private String motivo ;
  
  
  private LocalDate dataReferente ;
  
  
  private Long percentagem ;
  
  
  private LocalDateTime dataRegisto ;
  
  
  private String id ;
  
  
  private String estado ;

}