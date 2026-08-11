/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoEmissaoReqResponseDTO  {

  private Long missaoId;
  private String etapaAtual;
  private String etapaAtualDesc;          // descrição legível da etapa — ex.: "Processamento Logístico"
  private List<MissaoReqItemResponseDTO> requisicoes;

  // universo de colaboradores afetos à missão — popula o multiselect de associação a cada prestador
  private List<MissaoColaboradorResponseDTO> colaboradoresMissao;

}
