package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.MissaoValoresIniciaisDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetValoresIniciaisSubmissaoQueryHandler implements QueryHandler<GetValoresIniciaisSubmissaoQuery, ResponseEntity<MissaoValoresIniciaisDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetValoresIniciaisSubmissaoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetValoresIniciaisSubmissaoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<MissaoValoresIniciaisDTO> handle(GetValoresIniciaisSubmissaoQuery query) {

    LOGGER.debug("GetValoresIniciaisSubmissaoQuery: {}", query);
    return missaoServicoServiceRead.valoresIniciaisSubmissao(query);
  }

}
