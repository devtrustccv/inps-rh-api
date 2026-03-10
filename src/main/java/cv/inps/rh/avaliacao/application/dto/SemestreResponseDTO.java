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
public class SemestreResponseDTO {

  private String semestre;
  private BigDecimal avaliacaoFinal;
  private BigDecimal ponderacao;
  private BigDecimal classificacao;
}

