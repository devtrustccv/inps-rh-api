package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.DadosValidacaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class GetDadosValidacaoQueryHandler implements QueryHandler<GetDadosValidacaoQuery, ResponseEntity<List<DadosValidacaoDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosValidacaoQueryHandler.class);

  public GetDadosValidacaoQueryHandler() {

  }

  @IgrpQueryHandler
  public ResponseEntity<List<DadosValidacaoDTO>> handle(GetDadosValidacaoQuery query) {

    LOGGER.debug("GetDadosValidacaoQuery: {}", query);

    // TODO: Implement the query handling logic here
    return null;
  }

}
