package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRenovacaoContratoCommand implements Command {


  private RenovacaoContratoDTO renovacaocontrato;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <contratoId> is required")
  private String contratoId;

}
