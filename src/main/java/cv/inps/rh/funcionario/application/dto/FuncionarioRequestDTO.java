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
import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisReqDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioRequestDTO  {

  
  @Valid
  private DadosPessoaisReqDTO dadosPessoais ;
  
  @Valid
  private List<AgregadoDependenteReqDTO> familiares = new ArrayList<>();
  
  @Valid
  private DadosAcademicosProfReqDTO dadosAcademicosProf ;
  
  @Valid
  private DadosContratuaisReqDTO dadosContratuais ;
  
  @Valid
  private List<DadosBancariosReqDTO> dadosBancarios = new ArrayList<>();
  
  @Valid
  private List<AnexoReqDTO> anexos = new ArrayList<>();
  
  
  private EstadoValidacao validar ;

}