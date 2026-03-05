package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.notificacao.NotificacaoReadService;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetNotificacaoPedidoDeclaracaoQueryHandler implements QueryHandler<GetNotificacaoPedidoDeclaracaoQuery, ResponseEntity<NotificacaoInfoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacaoPedidoDeclaracaoQueryHandler.class);
  private final NotificacaoReadService notificacaoReadService;


   @IgrpQueryHandler
  public ResponseEntity<NotificacaoInfoDTO> handle(GetNotificacaoPedidoDeclaracaoQuery query) {

    LOGGER.debug("GetNotificacaoPedidoDeclaracaoQuery: {}", query);

    var response = notificacaoReadService.findByDeclaracaoId(query.getId());

    return ResponseEntity.ok(response);
  }

}
