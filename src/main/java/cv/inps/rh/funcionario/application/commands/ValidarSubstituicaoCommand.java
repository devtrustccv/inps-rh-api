package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarSubstituicaoCommand implements Command {

  
  private SubstituicaoDTO substituicao;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <substituicaoId> is required")
  private String substituicaoId;

}