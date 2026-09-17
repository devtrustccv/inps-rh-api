package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.WrapperListPrestadorServicoDTO;
import cv.inps.rh.missaoservico.application.services.PrestadorServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaPrestadorServicoQueryHandler implements QueryHandler<GetListaPrestadorServicoQuery, ResponseEntity<WrapperListPrestadorServicoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaPrestadorServicoQueryHandler.class);

  private final PrestadorServicoServiceRead prestadorServicoServiceRead;

  public GetListaPrestadorServicoQueryHandler(PrestadorServicoServiceRead prestadorServicoServiceRead) {
    this.prestadorServicoServiceRead = prestadorServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListPrestadorServicoDTO> handle(GetListaPrestadorServicoQuery query) {

    LOGGER.debug("GetListaPrestadorServicoQuery: {}", query);

    return prestadorServicoServiceRead.listar(query);
  }

}
