/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.application.dto.AnexoRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisResp2DTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioResponse2DTO  {

  
  @Valid
  private DadosPessoaisRespDTO dadosPessoais ;
  
  @Valid
  private List<AgregadoDependenteRespDTO> familiares = new ArrayList<>();
  
  @Valid
  private DadosAcademicosProfResponseDTO dadosAcademicosProf ;
  
  @Valid
  private DadosContratuaisResp2DTO dadosContratuais ;
  
  @Valid
  private List<DadosBancariosRespDTO> dadosBancarios = new ArrayList<>();
  
  @Valid
  private List<AnexoRespDTO> anexos = new ArrayList<>();

}