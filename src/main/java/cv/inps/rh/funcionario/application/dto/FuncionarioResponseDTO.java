/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
