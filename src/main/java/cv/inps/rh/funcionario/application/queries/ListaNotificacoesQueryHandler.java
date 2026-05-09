package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.notificacao.NotificacaoReadService;
import cv.inps.rh.shared.application.dto.WrapperListaNotificacoesDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListaNotificacoesQueryHandler implements QueryHandler<ListaNotificacoesQuery, ResponseEntity<WrapperListaNotificacoesDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ListaNotificacoesQueryHandler.class);
  private final NotificacaoReadService notificacaoReadService;

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaNotificacoesDTO> handle(ListaNotificacoesQuery query) {

    LOGGER.debug("ListaNotificacoesQuery: {}", query);

    WrapperListaNotificacoesDTO response = notificacaoReadService.findAll(query);

    return ResponseEntity.ok(response);
  }

}
