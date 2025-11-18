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
public class NovoContratoCommand implements Command {

  
  private NovoContratoDTO novocontrato;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}