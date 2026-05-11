package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.shared.application.dto.NotificacaoEnviarRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarNotificacaoCommand implements Command {


  private NotificacaoEnviarRequestDTO notificacaoenviarrequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
