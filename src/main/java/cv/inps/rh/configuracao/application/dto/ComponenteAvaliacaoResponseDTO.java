package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ComponenteAvaliacaoResponseDTO {

  private Long id;
  private String uuid;
  private Integer ano;
  private BigDecimal pesoComportamentais;
  private BigDecimal pesoTecnica;
  private BigDecimal ponderacaoObjetivo;
  private BigDecimal ponderacaoCompetencia;
  private BigDecimal ponderacaoAtitudePessoal;
  private String estado;
  private List<ObjectivoInpsLinhaResponseDTO> objectivosInps;
  private List<CompetenciaComportamentalLinhaResponseDTO> competenciasComportamentais;
  private List<CompetenciaTecnicaLinhaResponseDTO> competenciasTecnicas;
  private List<AtitudePessoalLinhaResponseDTO> atitudesPessoais;
}

