package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.shared.application.dto.NotificacaoEnviarRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarNotificacaoCommand implements Command {

  
  private NotificacaoEnviarRequestDTO notificacaoenviarrequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}