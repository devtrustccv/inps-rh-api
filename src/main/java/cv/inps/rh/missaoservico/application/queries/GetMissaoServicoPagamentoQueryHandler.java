package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.MissaoPagamentoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;

@Component
public class GetMissaoServicoPagamentoQueryHandler implements QueryHandler<GetMissaoServicoPagamentoQuery, ResponseEntity<MissaoPagamentoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMissaoServicoPagamentoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetMissaoServicoPagamentoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoPagamentoResponseDTO> handle(GetMissaoServicoPagamentoQuery query) {

    LOGGER.debug("GetMissaoServicoPagamentoQuery: {}", query);

    return missaoServicoServiceRead.getPagamento(query);
  }

}
