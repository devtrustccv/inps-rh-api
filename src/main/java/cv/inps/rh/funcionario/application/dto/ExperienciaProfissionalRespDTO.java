/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ExperienciaProfissionalRespDTO  {



  private Long id ;


  private String paisDesc ;


  private Long paisId ;


  private String uuid ;


  private String empresa ;


  private String cargo ;


  private LocalDate dataEntrada ;


  private LocalDate dataSaida ;


  private String observacoes ;


  private String estado ;

  private String estadoDesc ;

}
