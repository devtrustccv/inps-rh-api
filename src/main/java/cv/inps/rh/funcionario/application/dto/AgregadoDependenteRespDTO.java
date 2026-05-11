/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
public class AgregadoDependenteRespDTO  {


  private Long id ;


  private Long tipoDocumentoId ;


  private String tipoDocumentoDesc ;


  private String numDocumento ;


  private String nome ;


  private LocalDate dataNascimento ;


  private String genero ;


  private String grauParentesco ;


  private String dependente ;


  private String agregada ;


  private String responsavel ;


  private String estado ;

}
