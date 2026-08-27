package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.historicolaboral.AlterarEscalaoCargoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AlterarEscalaoCargoCommandHandler
      implements CommandHandler<AlterarEscalaoCargoCommand, ResponseEntity<SuccessResponseDTO>> {

   private final AlterarEscalaoCargoService alterarEscalaoCargoService;

   public AlterarEscalaoCargoCommandHandler(AlterarEscalaoCargoService alterarEscalaoCargoService) {
      this.alterarEscalaoCargoService = alterarEscalaoCargoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(AlterarEscalaoCargoCommand command) {
      var data = alterarEscalaoCargoService.alterar(command.getIdFuncionario(), command.getAlterarEscalaoCargo());
      return ResponseEntity.ok(data);
   }

}
