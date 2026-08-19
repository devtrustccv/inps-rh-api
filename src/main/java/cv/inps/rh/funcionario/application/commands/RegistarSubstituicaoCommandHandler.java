package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.SubstituicaoWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistarSubstituicaoCommandHandler implements CommandHandler<RegistarSubstituicaoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(RegistarSubstituicaoCommandHandler.class);

   private final SubstituicaoWriteService substituicaoWriteService;

   public RegistarSubstituicaoCommandHandler(SubstituicaoWriteService substituicaoWriteService) {
      this.substituicaoWriteService = substituicaoWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(RegistarSubstituicaoCommand command) {
      LOGGER.info("Registrar Substituição: {}", command);
      return  ResponseEntity.ok(substituicaoWriteService.registrar(command));
   }

}
