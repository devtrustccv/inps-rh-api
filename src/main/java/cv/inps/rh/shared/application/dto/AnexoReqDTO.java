/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AnexoReqDTO  {


  private Long id ;

  // Sem @NotNull: o formulário traz uma linha vazia por defeito. Quem grava decide com
  // DocumentoMapper.anexoPreenchidoOuErro (linha vazia ignorada, meio preenchida dá 400).
  private Long tipoDocumentoId ;


  private String documento ;

}
