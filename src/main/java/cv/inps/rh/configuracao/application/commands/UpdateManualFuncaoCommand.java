package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.configuracao.application.dto.ManualFuncaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateManualFuncaoCommand implements Command {

  
  private ManualFuncaoRequestDTO manualfuncaorequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}