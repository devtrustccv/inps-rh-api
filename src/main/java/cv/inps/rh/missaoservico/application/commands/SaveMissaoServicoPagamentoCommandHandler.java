package cv.inps.rh.missaoservico.application.commands;

import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveMissaoServicoPagamentoCommandHandler implements CommandHandler<SaveMissaoServicoPagamentoCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoServicoPagamentoCommandHandler.class);

   private final MissaoServicoServiceWrite missaoServicoServiceWrite;

   public SaveMissaoServicoPagamentoCommandHandler(MissaoServicoServiceWrite missaoServicoServiceWrite) {
      this.missaoServicoServiceWrite = missaoServicoServiceWrite;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(SaveMissaoServicoPagamentoCommand command) {

      LOGGER.debug("SaveMissaoServicoPagamentoCommand : {}", command);

      return missaoServicoServiceWrite.salvarPagamento(command);
   }

}
