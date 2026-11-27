/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessoDisciplinarResponseDTO  {



  private String processoDisciplinarId ;


  private String direcao ;


  private String seccao ;


  private String vinculo ;


  private String processoDisciplinar ;


  private String dataInicio ;


  private String dataFim ;


  // B18: campos do formulário (o ecrã de validação/edição pré-preenche-se com eles; antes vinha vazio).
  private String estado ;
  private String vinculoReferente ;
  private String numeroProcesso ;
  private String entidade ;
  private String tipoProcesso ;
  private String penaDisciplinar ;
  private String dataInicioPd ;
  private String dataFimPd ;
  private String dataInicioPena ;
  private String dataFimPena ;
  @com.fasterxml.jackson.annotation.JsonProperty("numeroBO")
  private String numeroBO ;
  @com.fasterxml.jackson.annotation.JsonProperty("datPublicacaoBO")
  private String datPublicacaoBO ;
  private String numeroOrdemServico ;
  private String dataOrdemServico ;
  private String numeroOfa ;
  private String dataEmissaoOfa ;
}
