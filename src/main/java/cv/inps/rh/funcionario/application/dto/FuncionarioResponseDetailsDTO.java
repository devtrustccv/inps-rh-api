/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioResponseDetailsDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private Integer tipoDocumentoId ;
  
  
  private String tipoDocumentoDesc ;
  
  
  private String numDocumento ;
  
  
  private String nome ;
  
  
  private String urlFoto ;
  
  
  private LocalDate dataNascimento ;
  
  
  private String genero ;
  
  
  private String nomeMae ;
  
  
  private String nomePai ;
  
  
  private String estadoCivil ;
  
  
  private String nacionalidade ;
  
  
  private Long naturalidadeId ;
  
  
  private String naturalidadeDesc ;
  
  
  private String nif ;
  
  
  private String numSegurado ;

}