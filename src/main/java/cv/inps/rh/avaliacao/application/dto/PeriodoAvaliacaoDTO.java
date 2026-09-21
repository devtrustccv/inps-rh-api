package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Resultado de um período — espelha RH_T_AVD_DETALHE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PeriodoAvaliacaoDTO {

  private String uuid;

  /** SEMESTRE1, TRIMESTRE3, ANUAL, ... */
  private String periodicidade;

  private String descricao;

  private BigDecimal avaliacaoObjectivo;
  private BigDecimal avaliacaoCompetencia;
  private BigDecimal avaliacaoAtitudePessoal;

  /** Expressão quantitativa do período. */
  private BigDecimal avaliacaoFinal;

  /** Expressão qualitativa, resolvida em RH_T_PARAM_ESCALA. */
  private String avaliacaoQualitativa;

  private String estado;

  private ObservacaoGeralDTO observacaoGeral;
  private ParecerColaboradorDTO parecerColaborador;
  private ComissaoExecutivaDTO comissaoExecutiva;

}
