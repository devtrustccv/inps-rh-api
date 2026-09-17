/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Gravação das avaliações do ecrã "Detalhe de Avaliação" — o botão Gravar envia as linhas todas. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoAvaliacoesRequestDTO {

  private List<MissaoAvaliacaoItemRequestDTO> avaliacoes;

}
