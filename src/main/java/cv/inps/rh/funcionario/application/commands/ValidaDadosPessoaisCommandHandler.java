package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidarDadosPessoaisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidaDadosPessoaisCommandHandler implements CommandHandler<ValidaDadosPessoaisCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidaDadosPessoaisCommandHandler.class);

   private final ValidarDadosPessoaisService validarDadosPessoaisService;
   public ValidaDadosPessoaisCommandHandler(ValidarDadosPessoaisService validarDadosPessoaisService) {

     this.validarDadosPessoaisService = validarDadosPessoaisService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidaDadosPessoaisCommand command) {
       LOGGER.info("Validando dados pessoais do funcionário: {}", command);
      return ResponseEntity.ok(validarDadosPessoaisService.executar(command));
   }

}
