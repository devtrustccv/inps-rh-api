package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.processamento.application.dto.MarcarNaoProcessadoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoverFuncionariosProcessamentoSalarialCommand implements Command {

  
  private MarcarNaoProcessadoRequestDTO marcarnaoprocessadorequest;

}