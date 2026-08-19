package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.RegimeWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdicionarRegimeTrabalhoCommandHandler implements CommandHandler<AdicionarRegimeTrabalhoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AdicionarRegimeTrabalhoCommandHandler.class);

   private final RegimeWriteService regimeWriteService;
   public AdicionarRegimeTrabalhoCommandHandler(RegimeWriteService regimeWriteService) {

     this.regimeWriteService = regimeWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(AdicionarRegimeTrabalhoCommand command) {

      return ResponseEntity.ok(regimeWriteService.alterarRegimeTrabalho(command));
   }

}
