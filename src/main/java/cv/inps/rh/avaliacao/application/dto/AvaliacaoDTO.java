/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class AvaliacaoDTO extends BaseAvaliacaoObjetivoDTO {



  private String nomeColaborador ;


  private UUID uuidColaborador ;

  private String estado;

  private List<ObjectivoAvaliacaoDTO> objectivos;
  private List<CompetenciaComportAvaliacaoDTO> competenciasComportamentais;
  private List<CompetenciaTecAvaliacaoDTO> competenciasTecnicas;
  private List<AtitudePessoalAvaliacaoDTO> atitudesPessoais;

}
