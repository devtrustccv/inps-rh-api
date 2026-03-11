package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoListagemResponseDTO {

  private String uuid;
  private Long funId;
  private UUID funUuid;
  private String nomeColaborador;
  private Long institId;                  // Direção
  private String nomeInstituicao;
  private Long cargoId;
  private String nomeCargo;
  private String estado;                  // 'A' | 'P' | 'C' → controla a cor e o tab
  private String semestreNota;            // texto composto ex: "1º Sem: 8.5 / 2º Sem: 7.2"
  private BigDecimal avaliacaoFinalSemestre1;
  private BigDecimal avaliacaoFinalSemestre2;
  private BigDecimal notaFinal;           // expressiva quantitativa (soma ponderada)
  private String notaFinalQualitativa;    // lookup RH_T_PARAM_ESCALA
}
