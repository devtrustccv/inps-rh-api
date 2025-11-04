/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.AnexoReqDTO;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioRequestDTO  {

  
  
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
  
  @Valid
  private List<AgregadoDependenteReqDTO> familiares = new ArrayList<>();
  
  @Valid
  private List<HabilitacaoLiterariaReqDTO> habilitacoesLiterarias = new ArrayList<>();
  
  @Valid
  private List<FormacaoProfissionalReqDTO> formacoesFeitas = new ArrayList<>();
  
  @Valid
  private List<ExperienciaProfissionalReqDTO> experienciasProfssionais = new ArrayList<>();
  
  @Valid
  private List<DadosBancariosReqDTO> dadosBancarios = new ArrayList<>();
  
  @Valid
  private List<AnexoReqDTO> anexos = new ArrayList<>();
  
  @Valid
  private DadosContratuaisReqDTO dadosContratuais ;
  
  @Valid
  private List<SubsidioReqDTO> subsidios = new ArrayList<>();
  
  @Valid
  private List<EncargosDescontosReqDTO> encargosDescontos = new ArrayList<>();
  
  
  private EstadoValidacao validar ;

}