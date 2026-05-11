package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.alerta.AlertaReadService;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetNotificacoesGeradasByAlertaQueryHandler implements QueryHandler<GetNotificacoesGeradasByAlertaQuery, ResponseEntity<List<NotificacaoInfoDTO>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacoesGeradasByAlertaQueryHandler.class);
    private final AlertaReadService alertaReadService;

    @IgrpQueryHandler
    public ResponseEntity<List<NotificacaoInfoDTO>> handle(GetNotificacoesGeradasByAlertaQuery query) {
        LOGGER.debug("GetNotificacoesGeradasByAlertaQuery: {}", query);
        var result = alertaReadService.findNotificacoesByAlertaId(query.getId());
        return ResponseEntity.ok(result);
    }
}
