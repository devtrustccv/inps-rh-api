package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.SubstituicaoWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;

@Component
public class RegistarSubstituicaoCommandHandler implements CommandHandler<RegistarSubstituicaoCommand, ResponseEntity<SubstituicaoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(RegistarSubstituicaoCommandHandler.class);

   private final SubstituicaoWriteService substituicaoWriteService;

   public RegistarSubstituicaoCommandHandler(SubstituicaoWriteService substituicaoWriteService) {
      this.substituicaoWriteService = substituicaoWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SubstituicaoDTO> handle(RegistarSubstituicaoCommand command) {
      LOGGER.info("Registrar Substituição: {}", command);
      return  ResponseEntity.ok(substituicaoWriteService.registrar(command));
   }

}
