/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

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
public class VinculoMovimentoResponseDTO  {


  private Long id ;

  private UUID uuid ;

  private Long tipoMovimentoId ;

  private String tipoMovimentoDescricao ;

  private String tipo ;

  private Integer percentagem ;

  private BigDecimal valor ;

  private String estado ;

}
