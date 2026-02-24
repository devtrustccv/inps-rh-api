/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioResponseDTO  {

  
  @Valid
  private DadosPessoaisRespDTO dadosPessoais ;
  
  @Valid
  private List<AgregadoDependenteRespDTO> familiares = new ArrayList<>();
  
  @Valid
  private DadosAcademicosProfResponseDTO dadosAcademicosProf ;
  
  @Valid
  private DadosContratuaisRespDTO dadosContratuais ;
  
  @Valid
  private List<DadosBancariosRespDTO> dadosBancarios = new ArrayList<>();
  
  @Valid
  private List<AnexoRespDTO> anexos = new ArrayList<>();

}