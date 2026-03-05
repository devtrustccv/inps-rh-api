package cv.inps.rh.configuracao.application.commands;

import cv.inps.rh.configuracao.domain.service.DocOutputService;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaveOutputDocumentCommandHandler implements CommandHandler<SaveOutputDocumentCommand, ResponseEntity<Map<String, ?>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveOutputDocumentCommandHandler.class);
    private final DocOutputService service;

    @IgrpCommandHandler
    public ResponseEntity<Map<String, ?>> handle(SaveOutputDocumentCommand command) {
        LOGGER.debug("SaveOutputDocumentCommand: {}", command);
        ParamDocOutputEntity entity = service.create(command.getDocoutputrequest());
        return ResponseEntity.ok(Map.of("id", entity.getUuid()));
    }
}
