package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDadosBancariosCommand implements Command {

  
  private ValidarDadosBancariosDTO validardadosbancarios;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}