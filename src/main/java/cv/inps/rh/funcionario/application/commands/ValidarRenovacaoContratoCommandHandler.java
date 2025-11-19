package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidacaoRenovacaoContratoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;

@Component
public class ValidarRenovacaoContratoCommandHandler implements CommandHandler<ValidarRenovacaoContratoCommand, ResponseEntity<RenovacaoContratoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRenovacaoContratoCommandHandler.class);

   private final ValidacaoRenovacaoContratoService validacaoRenovacaoContratoService;

   public ValidarRenovacaoContratoCommandHandler(ValidacaoRenovacaoContratoService validacaoRenovacaoContratoService) {

     this.validacaoRenovacaoContratoService = validacaoRenovacaoContratoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<RenovacaoContratoDTO> handle(ValidarRenovacaoContratoCommand command) {
      return ResponseEntity.ok(validacaoRenovacaoContratoService.validar(command));
   }

}
