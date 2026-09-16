/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Resposta da gravação da avaliação do prestador. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoPrestadorGravadaResponseDTO {

  private String id;             // uuid da avaliação
  private BigDecimal total;      // 0–100
  private String designacao;     // classe A–D

}
