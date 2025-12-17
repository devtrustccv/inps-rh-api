package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.service.SubstituicaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarSubstituicaoCommandHandler implements CommandHandler<ValidarSubstituicaoCommand, ResponseEntity<SubstituicaoDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarSubstituicaoCommandHandler.class);

   private final SubstituicaoWriteService substituicaoWriteService;

   public ValidarSubstituicaoCommandHandler(SubstituicaoWriteService substituicaoWriteService) {

     this.substituicaoWriteService = substituicaoWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SubstituicaoDTO> handle(ValidarSubstituicaoCommand command) {

      return ResponseEntity.ok().body(substituicaoWriteService.validar(command));
   }

}
