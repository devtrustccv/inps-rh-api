/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BaixaMedicaDetalheDTO  {


  private Long pedidoId ;


  private String pedidoUuid ;


  private String estado ;


  private Long tipoLicencaId ;


  private String tipoLicencaNome ;


  private Long motivoId ;


  private String motivoNome ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String observacao ;


  private BaixaMedicaCalculoDTO calculo ;

}
