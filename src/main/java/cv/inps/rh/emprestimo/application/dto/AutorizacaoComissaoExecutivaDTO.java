/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AutorizacaoComissaoExecutivaDTO  {

  @NotNull(message = "The field <parecer> is required")

  private ParecerProcesso parecer ;


  private String observacao ;
  @NotNull(message = "The field <data> is required")

  private LocalDate data ;
  @NotNull(message = "The field <action> is required")

  private ProcessStepAction action ;

}
