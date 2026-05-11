package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.ValidarDadosAcademicosDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDadosAcademicosCommand implements Command {


  private ValidarDadosAcademicosDTO validardadosacademicos;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}
