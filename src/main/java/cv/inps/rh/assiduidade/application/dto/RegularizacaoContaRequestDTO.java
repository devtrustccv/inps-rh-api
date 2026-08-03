/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class RegularizacaoContaRequestDTO {

  @NotBlank(message = "The field <mesReferencia> is required")
  private String mesReferencia;

  @NotNull(message = "The field <sdoRecebido> is required")
  private BigDecimal sdoRecebido;

  @NotNull(message = "The field <retroativoSalario> is required")
  private BigDecimal retroativoSalario;

  @NotNull(message = "The field <retroativoSdo> is required")
  private BigDecimal retroativoSdo;

  private String uuidRegularizacao;

  @NotNull(message = "The field <valorLiquido> is required")
  private Long valorLiquido;

  @NotNull(message = "The field <subsidiofiscalRecebido> is required")
  private Long subsidiofiscalRecebido;

  @NotNull(message = "The field <processamentoFuncionarioId> is required")
  private Long processamentoFuncionarioId;

  @NotNull(message = "The field <abonoBeneficioId> is required")
  private Long abonoBeneficioId;

  private String estado;
}
