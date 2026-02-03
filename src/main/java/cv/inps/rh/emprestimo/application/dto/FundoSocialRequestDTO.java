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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FundoSocialRequestDTO  {

  @NotBlank(message = "The field <funcionarioId> is required")

  private String funcionarioId ;
  @NotNull(message = "The field <tipoMovimentoId> is required")

  private Long tipoMovimentoId ;
  @NotNull(message = "The field <dataInicio> is required")

  private LocalDate dataInicio ;
  @NotNull(message = "The field <dataFim> is required")

  private LocalDate dataFim ;
  @NotNull(message = "The field <valorPrestacaoMensal> is required")

  private BigDecimal valorPrestacaoMensal ;
  @NotNull(message = "The field <valorTotalEmprestimo> is required")

  private BigDecimal valorTotalEmprestimo ;


  private BigDecimal valorPago ;


  private String finalidade ;

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

}
