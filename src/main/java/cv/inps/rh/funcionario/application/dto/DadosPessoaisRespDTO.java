/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ContactoRespDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoRespDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosPessoaisRespDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private Long tipoDocumentoId ;
  
  
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
  
  
  private Long nif ;
  
  
  private String numSegurado ;
  
  @Valid
  private List<ContactoRespDTO> contactos = new ArrayList<>();
  
  @Valid
  private EnderecoRespDTO endereco ;

}