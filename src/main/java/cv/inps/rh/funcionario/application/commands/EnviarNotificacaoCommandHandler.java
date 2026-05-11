package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.notificacao.NotificacaoWriteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EnviarNotificacaoCommandHandler implements CommandHandler<EnviarNotificacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnviarNotificacaoCommandHandler.class);
   private final NotificacaoWriteService notificacaoWriteService;

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(EnviarNotificacaoCommand command) {

      LOGGER.debug("EnviarNotificacaoCommand : {}", command);

      Map<String, ?> response = notificacaoWriteService.enviarNotificacao(command);

      return ResponseEntity.ok(response);
   }

}
