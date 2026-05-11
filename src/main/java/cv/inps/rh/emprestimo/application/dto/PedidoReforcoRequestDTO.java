/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoReforcoRequestDTO  {

  @NotBlank(message = "The field <emprestimoId> is required")

  private String emprestimoId ;


  private BigDecimal valorReforco ;


  private Long numeroPrestacao ;
  @NotBlank(message = "The field <tipoRenegociacao> is required")

  private String tipoRenegociacao ;


  private String motivoReforco ;

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();
  @NotNull(message = "The field <action> is required")

  private ProcessStepAction action ;

}
