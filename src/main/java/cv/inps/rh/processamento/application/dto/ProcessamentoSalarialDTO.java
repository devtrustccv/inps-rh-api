/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessamentoSalarialDTO {

  private Long id;


  private String estado;


  private String mesReferencia;


  private Long codigoCC;


  private String direcao;


  private String obs;


  private Long quantidade;


  private Long cabimento;


  private Long total;

}
