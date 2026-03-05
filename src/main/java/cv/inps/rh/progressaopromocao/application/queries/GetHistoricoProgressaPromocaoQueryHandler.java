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
public class GetHistoricoProgressaPromocaoQueryHandler implements QueryHandler<GetHistoricoProgressaPromocaoQuery, ResponseEntity<ListaProgressaoPromocaoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetHistoricoProgressaPromocaoQueryHandler.class);

  private final ProgressaoPromocaoReadService progressaoPromocaoReadService;

  public GetHistoricoProgressaPromocaoQueryHandler(ProgressaoPromocaoReadService progressaoPromocaoReadService) {

    this.progressaoPromocaoReadService = progressaoPromocaoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ListaProgressaoPromocaoDTO> handle(GetHistoricoProgressaPromocaoQuery query) {

    LOGGER.debug("GetHistoricoProgressaPromocaoQuery: {}", query);

    var data = progressaoPromocaoReadService.getHistoricoProgressaoPromocao(query);
    var response = new ListaProgressaoPromocaoDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());

    return ResponseEntity.ok(response);
  }

}
