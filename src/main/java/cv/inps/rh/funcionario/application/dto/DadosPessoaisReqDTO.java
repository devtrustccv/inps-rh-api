/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class DadosPessoaisReqDTO  {


  private Long idColaborador;

  @NotNull(message = "O campo Tipo de Documento é obrigatório")
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
