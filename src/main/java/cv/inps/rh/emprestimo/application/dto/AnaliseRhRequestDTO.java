/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AnaliseRhRequestDTO  {

  @NotNull(message = "The field <valorEmprestimo> is required")

  private Long valorEmprestimo ;
  @NotNull(message = "The field <numeroPrestacao> is required")

  private Long numeroPrestacao ;
  @NotNull(message = "The field <juros> is required")

  private Long juros ;
  @NotBlank(message = "The field <parecer> is required")

  private String parecer ;


  private String observacao ;

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

}
