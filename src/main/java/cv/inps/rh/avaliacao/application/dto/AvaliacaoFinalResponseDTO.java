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
public class AvaliacaoFinalResponseDTO {

  private SemestreResponseDTO semestre1;
  private SemestreResponseDTO semestre2;
  private BigDecimal expressivaQuantitativa;
  private String expressivaQualitativa;
}

