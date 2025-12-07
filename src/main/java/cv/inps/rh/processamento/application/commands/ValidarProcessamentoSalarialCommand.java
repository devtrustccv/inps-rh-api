package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.ValidarProcessamentoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarProcessamentoSalarialCommand implements Command {


  private ValidarProcessamentoRequestDTO validarprocessamentorequest;

}
