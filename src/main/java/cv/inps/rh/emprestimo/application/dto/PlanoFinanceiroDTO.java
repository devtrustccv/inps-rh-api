/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PlanoFinanceiroDTO  {



  private BigDecimal valorEmprestimo ;


  private BigDecimal taxaJuroAnual ;


  private Long periodoEmprestimo ;


  private LocalDate dataInicio ;


  private Long numeroPagamento ;


  private BigDecimal jurosTotal ;


  private BigDecimal custoTotalEmprestimo ;


  private BigDecimal pagamentoMensal ;

  @Valid
  private List<PlanoFinanceiroRowDTO> rows = new ArrayList<>();

}
