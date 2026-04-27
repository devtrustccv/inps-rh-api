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
public class ProcessamentoSalarioRequestDTO  {

  @NotNull(message = "The field <dataInicio> is required")
  
  private LocalDate dataInicio ;
  @NotNull(message = "The field <dataFim> is required")
  
  private LocalDate dataFim ;
  @NotBlank(message = "The field <tipo> is required")
  
  private String tipo ;
  
  
  private Long direccaoId ;
  
  
  private String observacao ;
  
  
  private Long relacionamentoId ;

}