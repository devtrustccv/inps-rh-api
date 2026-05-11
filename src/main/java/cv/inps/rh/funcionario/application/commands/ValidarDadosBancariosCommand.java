package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDadosBancariosCommand implements Command {


  private ValidarDadosBancariosDTO validardadosbancarios;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}
