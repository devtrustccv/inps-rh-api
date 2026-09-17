/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Avaliações dos prestadores de toda a missão — o ecrã "Detalhe de Avaliação", que mostra os
 * prestadores da missão numa só tabela. Uma linha por prestador <b>em cada processo</b>: o mesmo
 * prestador pode servir dois processos e é avaliado em cada um.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoAvaliacoesResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private String estadoMissao;                            // A | I | FINALIZADO
  private List<AvaliacaoPrestadorResponseDTO> avaliacoes;  // uma por prestador × processo
  private List<OpcaoDominioResponseDTO> opcoesAvaliacao;   // selects: 100 Muito Bom, 75 Bom, …

}
