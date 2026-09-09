package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.inps.rh.assiduidade.application.dto.ImportarMapaFeriaResultDTO;
import cv.inps.rh.assiduidade.application.services.MapaFeriaExcelImportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportarMapaFeriaCommandHandler
    implements CommandHandler<ImportarMapaFeriaCommand, ResponseEntity<ImportarMapaFeriaResultDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportarMapaFeriaCommandHandler.class);

  private final MapaFeriaExcelImportService mapaFeriaExcelImportService;

  @Override
  public ResponseEntity<ImportarMapaFeriaResultDTO> handle(ImportarMapaFeriaCommand command) {

    LOGGER.debug("ImportarMapaFeriaCommand: ano={}", command.getAno());

    return ResponseEntity.ok(
        mapaFeriaExcelImportService.importar(command.getFicheiro(), command.getAno()));
  }
}
