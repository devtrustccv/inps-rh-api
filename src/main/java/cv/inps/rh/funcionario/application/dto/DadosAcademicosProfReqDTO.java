/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosAcademicosProfReqDTO  {

  
  @Valid
  private List<HabilitacaoLiterariaReqDTO> habilitacoesLiterarias = new ArrayList<>();
  
  @Valid
  private List<FormacaoProfissionalReqDTO> formacoesFeitas = new ArrayList<>();
  
  @Valid
  private List<ExperienciaProfissionalReqDTO> experienciasProfssionais = new ArrayList<>();

}