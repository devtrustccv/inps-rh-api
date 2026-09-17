package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.ProcessoPrestadoresRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveProcessoPrestadoresCommand implements Command {

  private ProcessoPrestadoresRequestDTO processoprestadoresrequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;
  @NotBlank(message = "The field <tipoProcesso> is required")
  private String tipoProcesso;

}
