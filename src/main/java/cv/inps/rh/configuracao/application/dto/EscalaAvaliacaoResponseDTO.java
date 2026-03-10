package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EscalaAvaliacaoResponseDTO {

  private Long id;
  private String uuid;
  private Integer nivel;
  private String qualitativa;
  private String descricao;
  private BigDecimal quantitativaDe;
  private BigDecimal quantitativaAte;
  private String estado;
}

