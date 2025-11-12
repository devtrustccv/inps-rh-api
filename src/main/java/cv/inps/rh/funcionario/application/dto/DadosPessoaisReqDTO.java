/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosPessoaisReqDTO  {

  
  
  private Long tipoDocumentoId ;
  
  
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
  
  
  private Long nif ;
  
  
  private String numSegurado ;
  
  @Valid
  private List<ContactoReqDTO> contactos = new ArrayList<>();
  
  @Valid
  private EnderecoReqDTO endereco ;

}