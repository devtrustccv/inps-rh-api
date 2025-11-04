package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRegistoColaboradorCommand implements Command {

  
  private FuncionarioRequestDTO funcionariorequest;
  @NotNull(message = "The field <id> is required")
  private Long id;

}