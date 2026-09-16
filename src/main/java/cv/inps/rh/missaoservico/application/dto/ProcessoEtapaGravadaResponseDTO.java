/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resposta da gravação de uma etapa do processo de missão. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoEtapaGravadaResponseDTO {

  private String id;             // uuid do processo
  private String etapa;          // etapa do processo depois da gravação
  private String parecer;        // uuid do parecer — só nas etapas de parecer
  private String estadoMissao;   // estado da missão — só na autorização (FINALIZADO quando todos concluem)

}
