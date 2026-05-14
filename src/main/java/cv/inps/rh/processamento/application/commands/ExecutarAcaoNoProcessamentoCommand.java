package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.processamento.application.dto.ProcessamentoActionRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutarAcaoNoProcessamentoCommand implements Command {

  
  private ProcessamentoActionRequestDTO processamentoactionrequest;

}