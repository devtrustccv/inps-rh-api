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
public class CompetenciaAvaliacaoResponseDTO {

  private Long id;
  private String uuid;
  private Long avdId;
  private Long paramObjetivoId;
  private String componente;
  private Integer numeroOrdem;
  private String abrangencia;
  private String descricao;
  private BigDecimal peso;
  private BigDecimal ponderacao;
  private BigDecimal autoAvaliacao;
  private BigDecimal avaliacao;
}

