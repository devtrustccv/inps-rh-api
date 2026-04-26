package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.FosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SubstituirXmlCommandHandler implements CommandHandler<SubstituirXmlCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubstituirXmlCommandHandler.class);
  private final FosService fosService;

  public SubstituirXmlCommandHandler(FosService fosService) {
    this.fosService = fosService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SubstituirXmlCommand command) {

    LOGGER.debug("SubstituirXmlCommand : {}", command);

    fosService.substituirXml(command.getFosId());

    return ResponseEntity.ok().build();
  }

}
