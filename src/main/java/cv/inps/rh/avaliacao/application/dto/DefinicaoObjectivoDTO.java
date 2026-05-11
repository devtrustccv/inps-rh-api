/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@IgrpDTO
public class DefinicaoObjectivoDTO extends BaseAvaliacaoObjetivoDTO {

  private List<UUID> funUuids;

  private List<ObjectivoDTO> objectivos;
  private List<CompetenciaComportamentalDTO> competenciasComportamentais;
  private List<CompetenciaTecnicaDTO> competenciasTecnicas;
  private List<AtitudePessoalDTO> atitudesPessoais;

}
