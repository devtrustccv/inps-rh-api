package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDadosFamiliaresCommand implements Command {

  
  private ValidarAgregadosDependentesDTO validaragregadosdependentes;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}