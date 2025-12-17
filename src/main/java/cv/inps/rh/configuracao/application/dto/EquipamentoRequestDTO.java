/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EquipamentoRequestDTO {


  private String id;
  @NotBlank(message = "The field <idEquipamento> is required")

  private String idEquipamento;
  @NotBlank(message = "The field <descricaoLocal> is required")

  private String descricaoLocal;
  @NotBlank(message = "The field <descricaoTipoMovimento> is required")

  private String descricaoTipoMovimento;
  @NotBlank(message = "The field <ipAddress> is required")

  private String ipAddress;
  @NotNull(message = "The field <picagem> is required")

  private Integer picagem;
  @NotBlank(message = "The field <tipo> is required")

  private String tipo;
  @NotBlank(message = "The field <tipoMovimento> is required")

  private String tipoMovimento;

}
