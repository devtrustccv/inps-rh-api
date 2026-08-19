package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.historicolaboral.HistoricoLaboralWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AtualizarRelacaoLaboralCommandHandler
      implements CommandHandler<AtualizarRelacaoLaboralCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AtualizarRelacaoLaboralCommandHandler.class);

   private final HistoricoLaboralWriteService historicoLaboralWriteService;

   public AtualizarRelacaoLaboralCommandHandler(HistoricoLaboralWriteService historicoLaboralWriteService) {
      this.historicoLaboralWriteService = historicoLaboralWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(AtualizarRelacaoLaboralCommand command) {

      LOGGER.debug("AtualizarHistoricoLaboralCommand : {}", command);

      var data = historicoLaboralWriteService.atualizar(command);
      return ResponseEntity.ok(data);
   }

}
