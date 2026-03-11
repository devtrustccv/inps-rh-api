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
public class AvaliacaoResumoResponseDTO {

  private Long id;
  private String uuid;
  private Integer ano;
  private String semestre;
  private Long institId;
  private String institNome;
  private Long seccaoId;
  private String seccaoNome;
  private Long cargoId;
  private String cargoNome;
  private Long carrPccsId;
  private String carrPccsNome;
  private String estado;
  private Double avaliacaoFinal;
}

