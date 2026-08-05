package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.shared.application.dto.NotificarEnvioRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificarDestinatariosCommand implements Command {

  @Valid
  private NotificarEnvioRequestDTO notificarenviorequest;

}
