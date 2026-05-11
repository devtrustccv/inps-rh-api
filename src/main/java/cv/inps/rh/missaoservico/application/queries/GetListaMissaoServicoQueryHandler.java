package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.WrapperListMissaoServicoDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaMissaoServicoQueryHandler implements QueryHandler<GetListaMissaoServicoQuery, ResponseEntity<WrapperListMissaoServicoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaMissaoServicoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetListaMissaoServicoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListMissaoServicoDTO> handle(GetListaMissaoServicoQuery query) {

    LOGGER.debug("GetListaMissaoServicoQuery: {}", query);

    return missaoServicoServiceRead.getLista(query);
  }

}
