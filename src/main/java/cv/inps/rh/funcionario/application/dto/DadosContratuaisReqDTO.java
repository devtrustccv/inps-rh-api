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
public class DadosContratuaisReqDTO  {



  private Long tipoContrato ;


  private Long cargoPosicao ;


  private Long direcao ;


  private Long seccao ;


  private String centroCusto ;


  private Long carreira ;


  private Long categoria ;


  private Long escalaoReferencia ;


  private Long tipoVinculoLaboral ;


  private String RegimeTrabalho ;


  private BigDecimal salario ;


  private String moeda ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer duracaoMeses ;


  private Long localTrabalho ;


  private Long pais ;


  private Long ilha ;

}
