/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ParecerResponseDTO {

  private UUID uuid;
  private String responsavel;             // UGAL | COORDENADOR_RH | DIRECTOR_RH
  private String parecer;                 // FAVORAVEL | DESFAVORAVEL
  private String parecerDesc;
  private String observacao;
  private String estado;                  // P = rascunho | A = emitido | I = anulado (processo devolvido)
  private String estadoDesc;
  private String executadoPor;
  private LocalDate dataExecucao;

}
