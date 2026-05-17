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

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AumentoSalarialRequestDTO  {

  @NotBlank(message = "The field <designacao> is required")
  
  private String designacao ;
  @NotBlank(message = "The field <motivo> is required")
  
  private String motivo ;
  @NotNull(message = "The field <dataReferente> is required")
  
  private LocalDate dataReferente ;
  @NotNull(message = "The field <percentagem> is required")
  
  private Long percentagem ;

}