package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidarRenovacaoContratoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRenovacaoContratoCommand implements Command {

  
  private ValidarRenovacaoContratoDTO validarrenovacaocontrato;
  @NotBlank(message = "The field <contratoId> is required")
  private String contratoId;

}