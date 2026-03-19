package cv.inps.rh.missaoservico.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.missaoservico.application.dto.WrapperListMissaoServicoDTO;

@Component
public class GetListaMissaoServicoQueryHandler implements QueryHandler<GetListaMissaoServicoQuery, ResponseEntity<WrapperListMissaoServicoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaMissaoServicoQueryHandler.class);


  public GetListaMissaoServicoQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListMissaoServicoDTO> handle(GetListaMissaoServicoQuery query) {

    LOGGER.debug("GetListaMissaoServicoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}