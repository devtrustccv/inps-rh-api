package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.services.DocOutputService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeleteOutputDocumentCommandHandler implements CommandHandler<DeleteOutputDocumentCommand, ResponseEntity<Map<String, ?>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteOutputDocumentCommandHandler.class);
    private final DocOutputService service;

    @IgrpCommandHandler
    public ResponseEntity<Map<String, ?>> handle(DeleteOutputDocumentCommand command) {
        LOGGER.debug("DeleteOutputDocumentCommand : {}", command);
        var entity = service.delete(command.getId());
        return ResponseEntity.ok(Map.of("id", entity.getUuid()));
    }
}
