package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.ResponsavelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveResponsaveisDirecaoCommandHandler implements CommandHandler<SaveResponsaveisDirecaoCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveResponsaveisDirecaoCommandHandler.class);

  private final ResponsavelService responsavelService;

  public SaveResponsaveisDirecaoCommandHandler(ResponsavelService responsavelService) {
    this.responsavelService = responsavelService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveResponsaveisDirecaoCommand command) {

    LOGGER.debug("SaveResponsaveisDirecaoCommand : {}", command);

    responsavelService.saveResponsaveis(command.getAssociarresponsaveisrequest());

    return ResponseEntity.ok().build();
  }

}
