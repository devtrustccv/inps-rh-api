package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarRelacaoLaboralCommand implements Command {


  private RelacaoLaboralDTO relacaolaboral;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <tiprelUuid> is required")
  private String tiprelUuid;

}
