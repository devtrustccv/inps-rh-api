/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoPagamentoResponseDTO  {

  private Long missaoId;
  private String etapaAtual;              // campo etapa de RH_T_MISSAO_SERVICO
  private String estado;                  // A=Ativo
  private String referenciaPagamento;
  private LocalDate dataPagamento;

}
