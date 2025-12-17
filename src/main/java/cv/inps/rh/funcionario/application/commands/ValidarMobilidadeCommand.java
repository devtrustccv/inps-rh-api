package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarMobilidadeCommand implements Command {


  private MobilidadeDTO mobilidade;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <mobilidadeId> is required")
  private String mobilidadeId;

}
