/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class DetalhesEmprestimoDTO extends PedidoEmprestimoDTO {



  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private BigDecimal valorPrestacao ;


  private String cabimentacaoOrcamental ;


  private String avaliacaoTaxaEsforco ;

  @Valid
  private List<OutrosEmprestimosDTO> outrosEmprestimos = new ArrayList<>();

  @Valid
  private DecisaoEmprestimoDTO decisao ;

}
