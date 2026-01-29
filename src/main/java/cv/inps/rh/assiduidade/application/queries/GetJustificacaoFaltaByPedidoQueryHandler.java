package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.JustificarFaltaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;

@Component
public class GetJustificacaoFaltaByPedidoQueryHandler implements QueryHandler<GetJustificacaoFaltaByPedidoQuery, ResponseEntity<JustificarFaltaDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetJustificacaoFaltaByPedidoQueryHandler.class);

  private final JustificarFaltaReadService justificarFaltaReadService;

  public GetJustificacaoFaltaByPedidoQueryHandler(JustificarFaltaReadService justificarFaltaReadService) {

    this.justificarFaltaReadService = justificarFaltaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<JustificarFaltaDTO> handle(GetJustificacaoFaltaByPedidoQuery query) {

    LOGGER.debug("GetJustificacaoFaltaByPedidoQuery: {}", query);

    return ResponseEntity.ok(justificarFaltaReadService.getFaltaJustificada(query));
  }

}
