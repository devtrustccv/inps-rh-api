/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralMotivoRequestDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class SituacaoLaboralRequestDTO  {

  @NotBlank(message = "The field <codigo> is required")

  private String codigo ;
  @NotBlank(message = "The field <nome> is required")

  private String nome ;


  private String descricao ;


  private String tipoSituacaoLaboral ;


  private String tipoSituacao ;
  @NotBlank(message = "The field <estadoContrato> is required")

  private String estadoContrato ;


  private String remuneracao ;


  private String carreira ;


  private String tempoServico ;


  private String cessaVinculo ;


  private String suspendeProgressaoPromocao ;


  private String estado ;

  @Valid
  private List<SituacaoLaboralMotivoRequestDTO> associacao = new ArrayList<>();


  private String areaClassificacao ;


  private String afetaSituacaoLaboral ;


  private String abonoBeneficio ;


  private String ausenciaLocalTrabalho ;


  private String tipoAusencia ;


  private String tipoFalta ;


  private String descontoSalario ;


  private String tipoContagem ;


  private Integer numeroDias ;


  private Integer numeroDiasNaoDescontado ;


  private Integer numeroDiasDescontado ;


  private String regressaCarreiraOrigem ;

}
