package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EditarMobilidadeCommandHandler implements CommandHandler<EditarMobilidadeCommand, ResponseEntity<MobilidadeDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EditarMobilidadeCommandHandler.class);

   private final MobilidadeWriteService mobilidadeWriteService;
   public EditarMobilidadeCommandHandler(MobilidadeWriteService mobilidadeWriteService) {

     this.mobilidadeWriteService = mobilidadeWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<MobilidadeDTO> handle(EditarMobilidadeCommand command) {
       LOGGER.info("EditarMobilidadeCommandHandler.handle: {}", command);
      return ResponseEntity.ok(mobilidadeWriteService.editar(command));
   }

}
