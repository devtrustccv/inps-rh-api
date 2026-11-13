package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarMobilidadeCommand implements Command {

  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

  @NotBlank(message = "The field <mobilidadeId> is required")
  private String mobilidadeId;

}
