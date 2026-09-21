/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Linha-pai da grelha de avaliação: um colaborador num ano.
 *
 * <p>Os períodos deixaram de ser duas colunas fixas de semestre e passaram a ser a lista
 * {@link #periodos} — a grelha desdobra-a como linhas-filho.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoListagemResponseDTO {

  private String uuid;
  private Integer ano;
  private Long funId;
  private UUID funUuid;
  private String nomeColaborador;
  private Long institId;                  // Direção
  private String nomeInstituicao;
  private Long cargoId;
  private String cargoNome;
  private Long seccaoId;
  private String seccaoNome;
  private Long carrPccsId;
  private String carrPccsNome;

  /** INPS | DIRECAO | INDIVIDUAL. */
  private String abrangencia;

  private String estado;                  // 'A' | 'P' | 'C' → controla a cor e o tab

  /** Linhas-filho: um por período avaliado, pela ordem cronológica. */
  private List<PeriodoResumoDTO> periodos = new ArrayList<>();

  private BigDecimal notaFinal;           // expressiva quantitativa (soma ponderada)
  private String notaFinalQualitativa;

}
