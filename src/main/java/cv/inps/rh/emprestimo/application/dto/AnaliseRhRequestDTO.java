/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AnaliseRhRequestDTO  {

  @NotNull(message = "The field <valorEmprestimo> is required")

  private BigDecimal valorEmprestimo ;
  @NotNull(message = "The field <numeroPrestacao> is required")

  private Long numeroPrestacao ;
  @NotNull(message = "The field <juros> is required")

  private BigDecimal juros ;
  @NotNull(message = "The field <parecer> is required")

  private ParecerProcesso parecer ;


  private String observacao ;

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();
  @NotNull(message = "The field <action> is required")

  private ProcessStepAction action ;

}
