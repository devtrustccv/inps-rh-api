package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovaRelacaoLaboralCommand implements Command {

  
  private RelacaoLaboralDTO relacaolaboral;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}