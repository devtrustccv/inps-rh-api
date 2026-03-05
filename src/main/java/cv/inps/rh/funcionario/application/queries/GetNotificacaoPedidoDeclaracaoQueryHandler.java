package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.NotificacaoResponseDTO;
import cv.inps.rh.funcionario.application.service.notificacao.NotificacaoReadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetNotificacaoPedidoDeclaracaoQueryHandler implements QueryHandler<GetNotificacaoPedidoDeclaracaoQuery, ResponseEntity<NotificacaoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacaoPedidoDeclaracaoQueryHandler.class);
  private final NotificacaoReadService notificacaoReadService;


   @IgrpQueryHandler
  public ResponseEntity<NotificacaoResponseDTO> handle(GetNotificacaoPedidoDeclaracaoQuery query) {

    LOGGER.debug("GetNotificacaoPedidoDeclaracaoQuery: {}", query);

    NotificacaoResponseDTO response = notificacaoReadService.findByDeclaracaoId(query.getId());

    return ResponseEntity.ok(response);
  }

}