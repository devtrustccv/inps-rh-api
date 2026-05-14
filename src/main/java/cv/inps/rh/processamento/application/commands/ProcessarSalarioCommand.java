package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.processamento.application.dto.ProcessamentoSalarioRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessarSalarioCommand implements Command {

  
  private ProcessamentoSalarioRequestDTO processamentosalariorequest;

}