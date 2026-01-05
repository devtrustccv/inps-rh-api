/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class RelacaoLaboralDTO  {

  
  
  private EstadoValidacao validar ;
  
  
  private String ordemServico ;
  
  
  private String gerarOrdemServico ;
  
  
  private String situacaoAtual ;
  
  
  private String contrato ;
  
  
  private String vinculo ;
  
  
  private BigDecimal salario ;
  
  
  private String tipoMobilidade ;
  
  
  private Long direcao ;
  
  
  private Long secao ;
  
  
  private LocalDate dataInicioMobilidade ;
  
  
  private LocalDate dataFimMobilidade ;
  
  
  private Long localTrabalho ;
  
  
  private String pais ;
  
  
  private String ilha ;
  
  
  private Long cargo ;
  
  
  private String tipoAlteracaoCarreira ;
  
  
  private Long carreira ;
  
  
  private Long categoria ;
  
  
  private Long escalao ;
  
  
  private LocalDate dataInicioCarreira ;
  
  
  private LocalDate dataFimCarreira ;
  
  
  private Long situacaoLaboral ;
  
  
  private String motivo ;
  
  
  private LocalDate dataInicioSituacao ;
  
  
  private LocalDate dataFimSituacao ;
  
  
  private String observacao ;

}