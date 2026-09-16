/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Resposta da gravação em bloco: o total e a classe de cada linha gravada. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoAvaliacoesGravadasResponseDTO {

  private List<AvaliacaoPrestadorGravadaResponseDTO> avaliacoes;

}
