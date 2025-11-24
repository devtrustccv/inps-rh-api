package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.MobilidadeWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;

@Component
public class ValidarMobilidadeCommandHandler implements CommandHandler<ValidarMobilidadeCommand, ResponseEntity<MobilidadeDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarMobilidadeCommandHandler.class);

   private final MobilidadeWriteService mobilidadeWriteService;
   public ValidarMobilidadeCommandHandler(MobilidadeWriteService mobilidadeWriteService) {

     this.mobilidadeWriteService = mobilidadeWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<MobilidadeDTO> handle(ValidarMobilidadeCommand command) {
       LOGGER.info("ValidarMobilidadeCommandHandler.handle: {}", command);
      return ResponseEntity.ok(mobilidadeWriteService.validarMobilidade(command));
   }

}
