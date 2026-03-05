package cv.inps.rh.progressaopromocao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.progressaopromocao.application.dto.ListaProgressaoPromocaoDTO;
import cv.inps.rh.progressaopromocao.domain.service.ProgressaoPromocaoReadService;
import cv.inps.rh.shared.util.PageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaSimulacaoProgressaPromocaoQueryHandler implements QueryHandler<GetListaSimulacaoProgressaPromocaoQuery, ResponseEntity<ListaProgressaoPromocaoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaSimulacaoProgressaPromocaoQueryHandler.class);

  private final ProgressaoPromocaoReadService service;

  public GetListaSimulacaoProgressaPromocaoQueryHandler(ProgressaoPromocaoReadService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<ListaProgressaoPromocaoDTO> handle(GetListaSimulacaoProgressaPromocaoQuery query) {

    LOGGER.debug("GetListaSimulacaoProgressaPromocaoQuery: {}", query);

    var data = service.getSimulacaoProgressaoPromocaoSimulacao(query);
    var response = new ListaProgressaoPromocaoDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());

    return ResponseEntity.ok(response);
  }

}
