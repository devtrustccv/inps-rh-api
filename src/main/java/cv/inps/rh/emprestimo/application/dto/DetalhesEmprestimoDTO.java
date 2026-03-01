/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.DecisaoEmprestimoDTO;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.emprestimo.application.dto.OutrosEmprestimosDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
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
  
  
  private BigDecimal valorAdiantamento ;
  
  
  private String tipoSituacao ;
  
  
  private String nib ;
  
  
  private Long bancoId ;
  
  
  private Long numeroContaBanco ;
  
  
  private String swift ;
  
  
  private String motivo ;
  
  @Valid
  private List<OutrosEmprestimosDTO> outrosEmprestimos = new ArrayList<>();
  
  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();
  
  @Valid
  private DecisaoEmprestimoDTO decisao ;

}