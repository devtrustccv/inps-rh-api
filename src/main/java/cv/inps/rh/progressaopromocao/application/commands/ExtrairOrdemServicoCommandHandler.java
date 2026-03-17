package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ExtrairOrdemServicoCommandHandler implements CommandHandler<ExtrairOrdemServicoCommand, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtrairOrdemServicoCommandHandler.class);

  private final ProgressaoPromocaoWriteService progressaoPromocaoWriteService;

  public ExtrairOrdemServicoCommandHandler(ProgressaoPromocaoWriteService progressaoPromocaoWriteService) {
    this.progressaoPromocaoWriteService = progressaoPromocaoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<byte[]> handle(ExtrairOrdemServicoCommand command) {

    LOGGER.debug("ExtrairOrdemServicoCommand : {}", command);

    var bytes = progressaoPromocaoWriteService.extrairOrdemServico(command.getHistoricoids().getIds());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=os.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(bytes.length)
        .body(bytes);
  }

}
