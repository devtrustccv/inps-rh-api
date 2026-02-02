/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ElaboracaoContratoRequestDTO  {

  @NotNull(message = "The field <dataInicioEmprestimo> is required")

  private LocalDate dataInicioEmprestimo ;
  @NotNull(message = "The field <documentos> is required")
	@NotEmpty(message = "The field <documentos> must not be empty")
  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

}
