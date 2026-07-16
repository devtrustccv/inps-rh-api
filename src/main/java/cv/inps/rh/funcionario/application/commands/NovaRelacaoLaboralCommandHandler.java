package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NovaRelacaoLaboralCommandHandler implements CommandHandler<NovaRelacaoLaboralCommand, ResponseEntity<RelacaoLaboralDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(NovaRelacaoLaboralCommandHandler.class);

   private final HistoricoLaboralWriteService historicoLaboralWriteService;
   public NovaRelacaoLaboralCommandHandler(HistoricoLaboralWriteService historicoLaboralWriteService) {
      this.historicoLaboralWriteService = historicoLaboralWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<RelacaoLaboralDTO> handle(NovaRelacaoLaboralCommand command) {
      LOGGER.info("ValidarHistoricoLaboralCommandHandler.handle: {}", command);
      return ResponseEntity.ok(historicoLaboralWriteService.novo(command));
   }

}
