package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenovarContratoCommand implements Command {

  
  private RenovacaoContratoDTO renovacaocontrato;
  @NotBlank(message = "The field <id> is required")
  private String id;

}