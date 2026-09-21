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

  /**
   * Períodos a definir de uma vez. O ecrã dos objectivos comuns é um multiselect
   * ("Semestre 1 ×  Semestre 2 ×"), o do registo individual é um select simples — daí
   * aceitar-se tanto esta lista como o {@code periodicidade} singular herdado da base.
   * Quando os dois vêm preenchidos, esta lista manda.
   */
  private List<String> periodicidades;

  /**
   * Direções a abranger, para {@code abrangencia = DIRECAO}. O ecrã tem
   * "+ Adicionar Direção à Lista", pelo que uma gravação pode cobrir várias direções;
   * cada uma dá origem à sua linha em RH_T_AVD. Em alternativa aceita-se o
   * {@code institId} singular.
   */
  private List<Long> institIds;

  private List<UUID> funUuids;

  private List<ObjectivoDTO> objectivos;
  private List<CompetenciaComportamentalDTO> competenciasComportamentais;
  private List<CompetenciaTecnicaDTO> competenciasTecnicas;
  private List<AtitudePessoalDTO> atitudesPessoais;

}
