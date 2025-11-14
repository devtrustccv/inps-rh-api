package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequest2DTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRegistoColaboradorCommand implements Command {

  
  private FuncionarioRequest2DTO funcionariorequest2;
  @NotBlank(message = "The field <id> is required")
  private String id;

}