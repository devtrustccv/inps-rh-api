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
  private Long funId;
  private String nomeColaborador;
  private Integer ano;
  private String semestre;
  private Long institId;
  private Long seccaoId;
  private Long cargoId;
  private Long carrPccsId;
  private String estado;
  private BigDecimal avaliacaoFinal;
}

