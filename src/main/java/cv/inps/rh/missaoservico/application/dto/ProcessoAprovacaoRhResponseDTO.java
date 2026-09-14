/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoAprovacaoRhResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private MissaoProcessoResponseDTO processo;
  private ParecerResponseDTO parecerCoordenador;  // ciclo actual (rascunho ou emitido)
  private ParecerResponseDTO parecerDirector;     // ciclo actual (rascunho ou emitido)
  private ParecerResponseDTO parecerUgal;         // parecer UGAL que abriu esta aprovação (consulta)
  private List<ParecerResponseDTO> historico;     // todos os pareceres do Coordenador e do Director

}
