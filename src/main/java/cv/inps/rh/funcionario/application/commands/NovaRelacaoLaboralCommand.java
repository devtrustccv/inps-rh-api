package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovaRelacaoLaboralCommand implements Command {


  private RelacaoLaboralReqDTO relacaolaboral;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}
