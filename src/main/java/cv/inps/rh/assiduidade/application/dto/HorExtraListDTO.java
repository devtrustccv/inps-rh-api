/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class HorExtraListDTO  {



  private String funcionarioUuid ;


  private Long pedidoId ;

  private String pedidoUuid ;


  private String direcao ;


  private Long direcaoId ;


  private String nomeColaborador ;


  private String seccao ;


  private Long seccaoId ;


  private String dataInicio ;


  private String dataFim ;


  private String horasContratato ;


  private String horasTrabalho ;


  private BigDecimal salarioMensal ;


  private BigDecimal valorHorasMensal ;


  private BigDecimal valorHorasDiario ;


  private Integer percentagem ;


  private String estado ;


  private String estadoDesc ;

}
