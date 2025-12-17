package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.ValidarRemuneracaoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarNovoRemuneracaoCommand implements Command {


  private ValidarRemuneracaoRequestDTO validarremuneracaorequest;

}
