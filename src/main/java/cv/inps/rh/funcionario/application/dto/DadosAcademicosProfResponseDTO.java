/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalRespDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalRespDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaRespDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosAcademicosProfResponseDTO  {

  
  @Valid
  private List<HabilitacaoLiterariaRespDTO> habilitacoesLiterarias = new ArrayList<>();
  
  @Valid
  private List<FormacaoProfissionalRespDTO> formacoesFeitas = new ArrayList<>();
  
  @Valid
  private List<ExperienciaProfissionalRespDTO> experienciasProfssionais = new ArrayList<>();

}