/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CriterioAvaliacaoResponseDTO {

  private String criterio;                // SISTEMA_QUALIDADE | PRAZO_FORNECIMENTO | QUALIDADE_PRODUTO | CAPACIDADE_RESPOSTA | PRECO
  private Integer peso;                   // %
  private String avaliacao;               // valor escolhido: 100 | 75 | 50 | 25
  private String avaliacaoDesc;           // "Muito Bom", …
  private BigDecimal pontos;              // peso × avaliação

}
