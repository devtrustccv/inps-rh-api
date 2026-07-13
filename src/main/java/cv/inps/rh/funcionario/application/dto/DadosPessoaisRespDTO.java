/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosPessoaisRespDTO  {


  private Long id ;


  private Long idColaborador ;


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


  private String localidade ;


  private Long nif ;


  private String numSegurado ;

  @Valid
  private List<ContactoRespDTO> contactos = new ArrayList<>();

  @Valid
  private EnderecoRespDTO endereco ;

}
