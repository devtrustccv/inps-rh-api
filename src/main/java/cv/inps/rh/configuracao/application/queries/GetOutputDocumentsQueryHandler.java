package cv.inps.rh.configuracao.application.queries;

import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;
import cv.inps.rh.configuracao.domain.service.DocOutputService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetOutputDocumentsQueryHandler implements QueryHandler<GetOutputDocumentsQuery, ResponseEntity<WrapperDocOutputListDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOutputDocumentsQueryHandler.class);
    private final DocOutputService service;

    @IgrpQueryHandler
    public ResponseEntity<WrapperDocOutputListDTO> handle(GetOutputDocumentsQuery query) {
        LOGGER.debug("GetOutputDocumentsQuery: {}", query);
        WrapperDocOutputListDTO result = service.findAll(
                query.getTipoDocumento(),
                Integer.parseInt(query.getPageNumber()),
                Integer.parseInt(query.getPageSize())
        );
        return ResponseEntity.ok(result);
    }
}