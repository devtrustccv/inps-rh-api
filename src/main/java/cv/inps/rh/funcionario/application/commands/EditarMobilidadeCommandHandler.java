package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.MobilidadeWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EditarMobilidadeCommandHandler implements CommandHandler<EditarMobilidadeCommand, ResponseEntity<SuccessResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EditarMobilidadeCommandHandler.class);

   private final MobilidadeWriteService mobilidadeWriteService;
   public EditarMobilidadeCommandHandler(MobilidadeWriteService mobilidadeWriteService) {

     this.mobilidadeWriteService = mobilidadeWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(EditarMobilidadeCommand command) {
       LOGGER.info("EditarMobilidadeCommandHandler.handle: {}", command);
      return ResponseEntity.ok(mobilidadeWriteService.editar(command));
   }

}
