package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.ValidacaoRenovacaoContratoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarRenovacaoContratoCommandHandler implements CommandHandler<ValidarRenovacaoContratoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRenovacaoContratoCommandHandler.class);

   private final ValidacaoRenovacaoContratoService validacaoRenovacaoContratoService;

   public ValidarRenovacaoContratoCommandHandler(ValidacaoRenovacaoContratoService validacaoRenovacaoContratoService) {

     this.validacaoRenovacaoContratoService = validacaoRenovacaoContratoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ValidarRenovacaoContratoCommand command) {
      return ResponseEntity.ok(validacaoRenovacaoContratoService.validar(command));
   }

}
