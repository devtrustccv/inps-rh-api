package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Linha-filho da grelha de avaliação: um período de um colaborador num ano.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PeriodoResumoDTO {

  private String uuid;
  private String periodicidade;
  private String descricao;
  private BigDecimal avaliacaoFinal;
  private String avaliacaoQualitativa;
  private String estado;

}
