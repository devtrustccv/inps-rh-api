package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdicionarNovoRemuneracaoCommand implements Command {

  
  private NovoRemuneracaoRequestDTO novoremuneracaorequest;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}