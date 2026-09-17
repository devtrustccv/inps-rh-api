package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.PrestadorAvaliacaoResponseDTO;
import cv.inps.rh.missaoservico.application.services.PrestadorServicoServiceRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAvaliacoesPrestadorServicoQueryHandler implements QueryHandler<GetAvaliacoesPrestadorServicoQuery, ResponseEntity<List<PrestadorAvaliacaoResponseDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacoesPrestadorServicoQueryHandler.class);

  private final PrestadorServicoServiceRead prestadorServicoServiceRead;

  public GetAvaliacoesPrestadorServicoQueryHandler(PrestadorServicoServiceRead prestadorServicoServiceRead) {
    this.prestadorServicoServiceRead = prestadorServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<PrestadorAvaliacaoResponseDTO>> handle(GetAvaliacoesPrestadorServicoQuery query) {

    LOGGER.debug("GetAvaliacoesPrestadorServicoQuery: {}", query);

    return prestadorServicoServiceRead.avaliacoes(query);
  }

}
