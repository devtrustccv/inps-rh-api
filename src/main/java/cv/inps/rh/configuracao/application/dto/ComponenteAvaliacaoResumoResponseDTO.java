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
public class ComponenteAvaliacaoResumoResponseDTO {

  private Long id;
  private String uuid;
  private Integer ano;
  private String periodicidade;
  private BigDecimal pesoComportamentais;
  private BigDecimal pesoTecnica;
  private BigDecimal ponderacaoObjetivo;
  private BigDecimal ponderacaoCompetencia;
  private BigDecimal ponderacaoAtitudePessoal;
  private String estado;
  private Integer versao;

  /**
   * {@code false} quando já existe avaliação definida para o ano em RH_T_AVD —
   * a grelha esconde a ação de inativar. Ver ComponenteAvaliacaoService#listar.
   */
  private Boolean podeInativar;
}

