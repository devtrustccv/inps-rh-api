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
  private String semestre;
  private Long institId;
  private String instituicaoNome;
  private Long seccaoId;
  private Long seccaoNome;
  private Long cargoId;
  private Long cargoNome;
  private Long carrPccsId;
  private Long carrPccsNome;



}
