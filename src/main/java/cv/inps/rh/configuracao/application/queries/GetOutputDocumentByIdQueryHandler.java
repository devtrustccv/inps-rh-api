package cv.inps.rh.configuracao.application.queries;

import cv.inps.rh.configuracao.application.dto.DocOutputResponseDTO;
import cv.inps.rh.configuracao.application.services.DocOutputService;
import cv.inps.rh.configuracao.infrastructure.mappers.DocOutputMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetOutputDocumentByIdQueryHandler implements QueryHandler<GetOutputDocumentByIdQuery, ResponseEntity<DocOutputResponseDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOutputDocumentByIdQueryHandler.class);
    private final DocOutputService service;
    private final DocOutputMapper mapper;

    @IgrpQueryHandler
    public ResponseEntity<DocOutputResponseDTO> handle(GetOutputDocumentByIdQuery query) {
        LOGGER.debug("GetOutputDocumentByIdQuery: {}", query);
        ParamDocOutputEntity entity = service.findById(query.getId());
        return ResponseEntity.ok(mapper.toResponseDto(entity));
    }
}
