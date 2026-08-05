package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.notificacao.NotificarDestinatariosService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificarDestinatariosCommandHandler
    implements CommandHandler<NotificarDestinatariosCommand, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificarDestinatariosCommandHandler.class);

  private final NotificarDestinatariosService notificarDestinatariosService;

  @IgrpCommandHandler
  public ResponseEntity<Map<String, ?>> handle(NotificarDestinatariosCommand command) {

    LOGGER.debug("NotificarDestinatariosCommand : {}", command);

    Map<String, ?> response = notificarDestinatariosService.notificar(command.getNotificarenviorequest());

    return ResponseEntity.ok(response);
  }

}
