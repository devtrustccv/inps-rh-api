/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class PedidoEmprestimoDTO {

  @NotBlank(message = "The field <funcionarioId> is required")

  private String funcionarioId;

  @NotBlank(message = "The field <tipoSituacao> is required")
  private String tipoSituacao;

  private String marca;


  private Long anoFabrico;


  private String cilindrada;


  private String tipoviatura;


  private String combustivel;


  private String estadoViatura;


  private BigDecimal valorEmprestimo;


  private Long numeroPrestacoes;


  private BigDecimal juros;

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

}
