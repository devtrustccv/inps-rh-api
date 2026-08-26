package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.historicolaboral.AlterarEscalaoCargoService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidarEscalaoCargoCommandHandler
      implements CommandHandler<ValidarEscalaoCargoCommand, ResponseEntity<SuccessResponseDTO>> {

   private final AlterarEscalaoCargoService alterarEscalaoCargoService;

   public ValidarEscalaoCargoCommandHandler(AlterarEscalaoCargoService alterarEscalaoCargoService) {
      this.alterarEscalaoCargoService = alterarEscalaoCargoService;
   }

   @IgrpCommandHandler
   public ResponseEntity<SuccessResponseDTO> handle(ValidarEscalaoCargoCommand command) {
      var data = alterarEscalaoCargoService.validar(
          command.getIdFuncionario(), command.getTiprelUuid(), command.getAlterarEscalaoCargo());
      return ResponseEntity.ok(data);
   }

}
