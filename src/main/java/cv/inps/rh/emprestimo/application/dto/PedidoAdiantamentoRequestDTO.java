/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoAdiantamentoRequestDTO  {

  @NotBlank(message = "The field <emprestimoId> is required")
  
  private String emprestimoId ;
  
  
  private BigDecimal valorAdiantamento ;
  
  
  private Long numeroPrestacao ;
  @NotBlank(message = "The field <tipoSituacao> is required")
  
  private String tipoSituacao ;
  @NotNull(message = "The field <action> is required")
  
  private ProcessStepAction action ;
  
  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

}