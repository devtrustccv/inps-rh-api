package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.ManualFuncaoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateManualFuncaoCommand implements Command {


  private ManualFuncaoRequestDTO manualfuncaorequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
