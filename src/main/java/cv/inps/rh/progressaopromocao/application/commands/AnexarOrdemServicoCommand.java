package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexarOrdemServicoCommand implements Command {

  
  private AnexarOrdemServicoRequestDTO anexarordemservicorequest;

}