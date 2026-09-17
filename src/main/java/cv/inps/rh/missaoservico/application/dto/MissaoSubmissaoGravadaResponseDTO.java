/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resposta da gravação da submissão (criar e editar a missão). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoSubmissaoGravadaResponseDTO {

  private String id;                  // uuid da missão
  private Long nrMissao;              // sequencial dentro do ano
  private String nrMissaoFormatado;   // "nr/ano"

}
