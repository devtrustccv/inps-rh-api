package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidarRemuneracaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarNovoRemuneracaoCommand implements Command {

  
  private ValidarRemuneracaoRequestDTO validarremuneracaorequest;

}