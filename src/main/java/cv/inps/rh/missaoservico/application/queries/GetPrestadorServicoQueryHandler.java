package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.PrestadorServicoResponseDTO;
import cv.inps.rh.missaoservico.application.services.PrestadorServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetPrestadorServicoQueryHandler implements QueryHandler<GetPrestadorServicoQuery, ResponseEntity<PrestadorServicoResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPrestadorServicoQueryHandler.class);

  private final PrestadorServicoServiceRead prestadorServicoServiceRead;

  public GetPrestadorServicoQueryHandler(PrestadorServicoServiceRead prestadorServicoServiceRead) {
    this.prestadorServicoServiceRead = prestadorServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<PrestadorServicoResponseDTO> handle(GetPrestadorServicoQuery query) {

    LOGGER.debug("GetPrestadorServicoQuery: {}", query);

    return prestadorServicoServiceRead.detalhe(query);
  }

}
