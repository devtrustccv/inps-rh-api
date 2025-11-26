package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.ValidacaoDadosPessoaisDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaDadosPessoaisCommand implements Command {


  private ValidacaoDadosPessoaisDTO validacaodadospessoais;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}
