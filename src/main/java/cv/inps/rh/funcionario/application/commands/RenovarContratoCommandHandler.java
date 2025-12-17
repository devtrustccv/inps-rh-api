package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.service.RenovacaoContratoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RenovarContratoCommandHandler implements CommandHandler<RenovarContratoCommand, ResponseEntity<RenovacaoContratoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(RenovarContratoCommandHandler.class);

   private final RenovacaoContratoService renovacaoContratoService;

   public RenovarContratoCommandHandler(RenovacaoContratoService renovacaoContratoService) {

     this.renovacaoContratoService = renovacaoContratoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<RenovacaoContratoDTO> handle(RenovarContratoCommand command) {

      return ResponseEntity.ok(renovacaoContratoService.renovarContrato(command));
   }

}
