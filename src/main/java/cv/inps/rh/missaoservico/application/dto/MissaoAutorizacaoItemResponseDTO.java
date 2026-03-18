/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoAutorizacaoItemResponseDTO  {

  private Long logisticaId;
  private String referencia;
  private String nome;
  private BigDecimal valorTotal;
  private Long numeroCabimento;           // MissaoLogisticaEntity.cabId
  private String estadoCabimento;

}
