/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ValidarNovoHistoricoLaboralDTO  {



  private EstadoValidacao validar ;


  private String estado ;


  private String tipoAlteracao ;

  @Valid
  private DadosContratuaisReqDTO dadosContratuais ;


  private String tipoOrdemServico ;


  private String gerarOrdemServico ;

}
