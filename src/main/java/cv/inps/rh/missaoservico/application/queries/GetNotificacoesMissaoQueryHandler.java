package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.missaoservico.application.dto.NotificacaoMissaoResponseDTO;
import cv.inps.rh.missaoservico.application.services.MissaoServicoServiceRead;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetNotificacoesMissaoQueryHandler
    implements QueryHandler<GetNotificacoesMissaoQuery, ResponseEntity<List<NotificacaoMissaoResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificacoesMissaoQueryHandler.class);

  private final MissaoServicoServiceRead missaoServicoServiceRead;

  public GetNotificacoesMissaoQueryHandler(MissaoServicoServiceRead missaoServicoServiceRead) {
    this.missaoServicoServiceRead = missaoServicoServiceRead;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<NotificacaoMissaoResponseDTO>> handle(GetNotificacoesMissaoQuery query) {

    LOGGER.debug("GetNotificacoesMissaoQuery: {}", query);

    return missaoServicoServiceRead.listarNotificacoes(IdentificadorUnico.from(query.getUuid()).valor());
  }

}
