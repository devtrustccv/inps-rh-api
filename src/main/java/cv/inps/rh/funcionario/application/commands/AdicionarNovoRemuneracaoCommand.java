package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdicionarNovoRemuneracaoCommand implements Command {


  private NovoRemuneracaoRequestDTO novoremuneracaorequest;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}
