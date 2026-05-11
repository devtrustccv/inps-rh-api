package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;
import cv.inps.rh.assiduidade.application.services.FeriaReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetPedidoFeriaQueryHandler implements QueryHandler<GetPedidoFeriaQuery, ResponseEntity<PedidoFeriaAlterarReqDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPedidoFeriaQueryHandler.class);

  private final FeriaReadService feriaReadService;

  public GetPedidoFeriaQueryHandler(FeriaReadService feriaReadService) {

    this.feriaReadService = feriaReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<PedidoFeriaAlterarReqDTO> handle(GetPedidoFeriaQuery query) {

    LOGGER.debug("GetPedidoFeriaQuery: {}", query);

    return ResponseEntity.ok(feriaReadService.getPedidoFeria(query));
  }

}
