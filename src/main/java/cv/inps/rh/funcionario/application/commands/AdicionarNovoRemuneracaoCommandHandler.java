package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdicionarNovoRemuneracaoCommandHandler implements CommandHandler<AdicionarNovoRemuneracaoCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdicionarNovoRemuneracaoCommandHandler.class);

  private final RenumeracoesWriteService renumeracoesWriteService;

  public AdicionarNovoRemuneracaoCommandHandler(RenumeracoesWriteService renumeracoesWriteService) {
    this.renumeracoesWriteService = renumeracoesWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(AdicionarNovoRemuneracaoCommand command) {

    LOGGER.info("Handling novo remuneracao command with request: {}", command);

    return ResponseEntity.ok(
        renumeracoesWriteService.novoRemuneracao(command.getFuncionarioId(), command.getNovoremuneracaorequest()));
  }

}
