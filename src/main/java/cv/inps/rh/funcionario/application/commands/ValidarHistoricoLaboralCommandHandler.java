package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarHistoricoLaboralCommandHandler implements CommandHandler<ValidarHistoricoLaboralCommand, ResponseEntity<ValidarNovoHistoricoLaboralDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarHistoricoLaboralCommandHandler.class);

   private final HistoricoLaboralWriteService historicoLaboralWriteService;
   public ValidarHistoricoLaboralCommandHandler(HistoricoLaboralWriteService historicoLaboralWriteService) {
      this.historicoLaboralWriteService = historicoLaboralWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<ValidarNovoHistoricoLaboralDTO> handle(ValidarHistoricoLaboralCommand command) {
      LOGGER.info("ValidarHistoricoLaboralCommandHandler.handle: {}", command);
      return ResponseEntity.ok(historicoLaboralWriteService.validar(command));
   }

}
