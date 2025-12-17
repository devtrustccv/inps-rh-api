package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovoProcessoDisciplinarCommand implements Command {


  private ProcessoDisciplinarRequestDTO processodisciplinarrequest;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}
