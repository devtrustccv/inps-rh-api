/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosContratuaisRespDTO  {

  
  
  private Long tipoContratoId ;
  
  
  private String tipoContratoDesc ;
  
  
  private Long cargoPosicaoId ;
  
  
  private String cargoPosicaoDesc ;
  
  
  private Long direcaoId ;
  
  
  private String direcaoDesc ;
  
  
  private Long seccaoId ;
  
  
  private String seccaoDesc ;
  
  
  private String centroCusto ;
  
  
  private Long carreiraId ;
  
  
  private String carreiraDesc ;
  
  
  private Long categoriaId ;
  
  
  private String categoriaDesc ;
  
  
  private Long escalaoReferenciaId ;
  
  
  private String escalaoReferenciaDesc ;
  
  
  private Long tipoVinculoLaboralId ;
  
  
  private String tipoVinculoLaboralDesc ;
  
  
  private String RegimeTrabalho ;
  
  
  private BigDecimal salario ;
  
  
  private String moeda ;
  
  
  private LocalDate dataInicio ;
  
  
  private LocalDate dataFim ;
  
  
  private Integer duracaoMeses ;
  
  
  private Long localTrabalhoId ;
  
  
  private String localTrabalhoDesc ;
  
  
  private String pais ;
  
  
  private String ilha ;

}