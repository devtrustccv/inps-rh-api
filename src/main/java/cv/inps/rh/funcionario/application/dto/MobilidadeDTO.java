/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MobilidadeDTO  {



  private EstadoValidacao validar ;


  private String tipoMobilidade ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String dirrecaoAntes ;


  private Long direcaoDepois ;


  private String seccaoAntes ;


  private Long seccaoDepois ;


  private String localTrabalhoAntes ;


  private Long localTrabalhoDepois ;

}
