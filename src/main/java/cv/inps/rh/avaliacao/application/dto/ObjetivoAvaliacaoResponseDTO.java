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
public class ObjetivoAvaliacaoResponseDTO {

  private Long id;
  private String uuid;
  private Long avdId;
  private Long paramObjetivoId;
  private Integer numeroOrdem;
  private String abrangencia;
  private String descricao;
  private String kpi;
  private String meta;
  private BigDecimal ponderacao;
  private String autoRealizado;
  private BigDecimal autoAvaliacao;
  private String realizado;
  private BigDecimal avaliacao;
}

