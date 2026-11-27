/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessoDisciplinarRequestDTO  {

  @NotBlank(message = "The field <vinculoReferente> is required")

  private String vinculoReferente ;
  private String numeroProcesso ;

  @NotBlank(message = "The field <entidade> is required")

  private String entidade ;

  @NotBlank(message = "The field <tipoProcesso> is required")

  private String tipoProcesso ;
  private String estadoProcesso ;


  private String penaDisciplinar ;


  private String dataInicioPd ;


  private String dataFimPd ;


  private String dataInicioPena ;


  private String dataFimPena ;


  private String numeroOrdemServico ;


  private String dataOrdemServico ;

  // BO / OFA (o ecrã envia estes nomes; sem eles o backend descartava-os — B17).
  @com.fasterxml.jackson.annotation.JsonProperty("numeroBO")
  private String numeroBO ;

  @com.fasterxml.jackson.annotation.JsonProperty("datPublicacaoBO")
  private String datPublicacaoBO ;

  @com.fasterxml.jackson.annotation.JsonProperty("numeroOfa")
  private String numeroOfa ;

  @com.fasterxml.jackson.annotation.JsonProperty("dataEmissaoOfa")
  private String dataEmissaoOfa ;


  private String validar ;

}
