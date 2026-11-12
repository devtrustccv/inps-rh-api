package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.MarcadoresDocumentoDTO;
import cv.inps.rh.configuracao.application.services.DocOutputService;
import cv.inps.rh.shared.domain.service.documento.DocumentoSaidaRenderer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class GetMarcadoresDocumentoQueryHandler implements QueryHandler<GetMarcadoresDocumentoQuery, ResponseEntity<MarcadoresDocumentoDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMarcadoresDocumentoQueryHandler.class);
    private final DocOutputService service;
    private final DocumentoSaidaRenderer renderer;

    @IgrpQueryHandler
    @Transactional(readOnly = true)
    public ResponseEntity<MarcadoresDocumentoDTO> handle(GetMarcadoresDocumentoQuery query) {
        LOGGER.debug("GetMarcadoresDocumentoQuery: {}", query);
        var doc = service.findById(query.getId());
        return ResponseEntity.ok(new MarcadoresDocumentoDTO(doc.getTipoDocumento(), new ArrayList<>(renderer.marcadores(doc))));
    }
}
