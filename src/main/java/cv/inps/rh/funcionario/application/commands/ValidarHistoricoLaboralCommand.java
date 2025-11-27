package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarHistoricoLaboralCommand implements Command {

  
  private ValidarNovoHistoricoLaboralDTO validarnovohistoricolaboral;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}