package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ComponenteAvaliacaoRequestDTO {

  @NotNull
  private Integer ano;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("100")
  private BigDecimal pesoComportamentais;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("100")
  private BigDecimal pesoTecnica;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("100")
  private BigDecimal ponderacaoObjetivo;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("100")
  private BigDecimal ponderacaoCompetencia;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("100")
  private BigDecimal ponderacaoAtitudePessoal;

  @NotEmpty
  private List<ObjectivoInpsLinhaRequestDTO> objectivosInps;

  @NotEmpty
  private List<CompetenciaComportamentalLinhaRequestDTO> competenciasComportamentais;

  @NotEmpty
  private List<CompetenciaTecnicaLinhaRequestDTO> competenciasTecnicas;

  @NotEmpty
  private List<AtitudePessoalLinhaRequestDTO> atitudesPessoais;
}

