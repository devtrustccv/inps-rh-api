package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.domain.service.DocOutputService;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UpdateOutputDocumentCommandHandler implements CommandHandler<UpdateOutputDocumentCommand, ResponseEntity<Map<String, ?>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateOutputDocumentCommandHandler.class);
    private final DocOutputService service;

    @IgrpCommandHandler
    public ResponseEntity<Map<String, ?>> handle(UpdateOutputDocumentCommand command) {
        LOGGER.debug("UpdateOutputDocumentCommand : {}", command);
        ParamDocOutputEntity entity = service.update(command.getId(), command.getDocoutputrequest());
        return ResponseEntity.ok(Map.of("id", entity.getUuid()));
    }
}
