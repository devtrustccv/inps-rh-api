package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.NovoContratoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarContratoCommand implements Command {

  
  private NovoContratoDTO novocontrato;
  @NotBlank(message = "The field <id> is required")
  private String id;
  @NotBlank(message = "The field <contratoId> is required")
  private String contratoId;

}