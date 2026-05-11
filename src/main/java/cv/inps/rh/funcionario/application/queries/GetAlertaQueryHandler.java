package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.AlertaDTO;
import cv.inps.rh.funcionario.application.service.alerta.AlertaReadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAlertaQueryHandler implements QueryHandler<GetAlertaQuery, ResponseEntity<AlertaDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAlertaQueryHandler.class);
    private final AlertaReadService alertaReadService;

    @IgrpQueryHandler
    public ResponseEntity<AlertaDTO> handle(GetAlertaQuery query) {
        LOGGER.debug("GetAlertaQuery: {}", query);
        AlertaDTO result = alertaReadService.findById(query.getId());
        return ResponseEntity.ok(result);
    }
}
