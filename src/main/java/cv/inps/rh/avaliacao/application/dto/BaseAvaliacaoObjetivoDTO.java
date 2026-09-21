/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BaseAvaliacaoObjetivoDTO  {


  private Long id;
  private String uuid;
  private Integer ano;
  private BigDecimal pesoComportamentais;
  private BigDecimal pesoTecnica;
  /**
   * Período concreto do ciclo: SEMESTRE1, TRIMESTRE3, ANUAL, ... (domínio PERIODICIDADE).
   * Substitui o antigo {@code semestre} ('1'|'2').
   */
  private String periodicidade;

  /**
   * Períodos já definidos para esta avaliação. Só de leitura — é o que permite ao ecrã
   * saber quais os separadores/períodos a mostrar sem ter de adivinhar pelo ciclo.
   */
  private java.util.List<String> periodicidadesDefinidas;

  /** INPS | DIRECAO | INDIVIDUAL. Em branco assume INDIVIDUAL. */
  private String abrangencia;
  private Long institId;
  private String instituicaoNome;
  private Long seccaoId;
  private String seccaoNome;
  private Long cargoId;
  private String cargoNome;
  private Long carrPccsId;
  private String carrPccsNome;



}
