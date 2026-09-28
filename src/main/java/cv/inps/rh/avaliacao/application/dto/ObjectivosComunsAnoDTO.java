package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Linha pai da lista "Objectivos / Avaliação Comuns": um ano com objectivos comuns e, como
 * filhos, os períodos já avaliados (RH_T_AVD_PERIODICIDADE.PERIODICIDADE).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjectivosComunsAnoDTO {

  private Integer ano;
  private List<PeriodoAvaliadoDTO> periodos = new ArrayList<>();

}
