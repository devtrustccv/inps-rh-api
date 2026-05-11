package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListAlertaDTO;
import cv.inps.rh.funcionario.application.service.alerta.AlertaReadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListAlertaQueryHandler implements QueryHandler<GetListAlertaQuery, ResponseEntity<WrapperListAlertaDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetListAlertaQueryHandler.class);
    private final AlertaReadService alertaReadService;

    @IgrpQueryHandler
    public ResponseEntity<WrapperListAlertaDTO> handle(GetListAlertaQuery query) {
        LOGGER.debug("GetListAlertaQuery: {}", query);
        WrapperListAlertaDTO result = alertaReadService.findAll(query);
        return ResponseEntity.ok(result);
    }
}
