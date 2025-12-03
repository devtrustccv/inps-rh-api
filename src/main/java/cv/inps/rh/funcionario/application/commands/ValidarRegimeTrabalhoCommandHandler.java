package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;
import cv.inps.rh.funcionario.application.service.RegimeWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarRegimeTrabalhoCommandHandler implements CommandHandler<ValidarRegimeTrabalhoCommand, ResponseEntity<RegimeTrabalhoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRegimeTrabalhoCommandHandler.class);

   private final RegimeWriteService regimeWriteService;

   public ValidarRegimeTrabalhoCommandHandler(RegimeWriteService regimeWriteService) {

     this.regimeWriteService = regimeWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<RegimeTrabalhoDTO> handle(ValidarRegimeTrabalhoCommand command) {

      return ResponseEntity.ok(regimeWriteService.validar(command));
   }

}
