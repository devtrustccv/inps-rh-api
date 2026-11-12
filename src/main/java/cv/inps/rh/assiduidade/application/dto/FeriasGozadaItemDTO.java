/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FeriasGozadaItemDTO  {



  private Long id ;


  private String uuid ;


  private String pedidoUuid ;


  private String tipoPedido ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer numDias ;


  private String estado ;


  private String estadoDesc ;


  private String observacao ;

}
