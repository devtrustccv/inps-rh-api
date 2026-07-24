package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.CarreiraNovoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarCarreiraCommand implements Command {


  private CarreiraNovoDTO dadoscontratuaisreq;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;
  @NotBlank(message = "The field <carreiraId> is required")
  private String carreiraId;

}
