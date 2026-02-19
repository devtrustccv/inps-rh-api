/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DecisaoEmprestimoDTO  {


  @Valid
  private BaseDecisaoDTO analiseRhPedido ;

  @Valid
  private BaseDecisaoDTO analiseFinanceiroPedido ;

  @Valid
  private BaseDecisaoDTO autorizacaoComissaoExecutivaPedido ;

  @Valid
  private BaseDecisaoDTO analiseRhAdiantamento ;

  @Valid
  private BaseDecisaoDTO verificacaoAdiantamento ;

  @Valid
  private BaseDecisaoDTO analiseRhRenegociacao ;

  @Valid
  private BaseDecisaoDTO analiseFinanceiroRenegociacao ;

  @Valid
  private BaseDecisaoDTO autorizacaoComissaoExecutivaRenegociacao ;

}
