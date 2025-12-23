package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.service.documento.DocumentoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetOrdemServicoQueryHandler implements QueryHandler<GetOrdemServicoQuery, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetOrdemServicoQueryHandler.class);

  private final DocumentoWriteService documentoWriteService;

  public GetOrdemServicoQueryHandler(DocumentoWriteService documentoWriteService) {
    this.documentoWriteService = documentoWriteService;
  }

  @IgrpQueryHandler
  public ResponseEntity<String> handle(GetOrdemServicoQuery query) {

    LOGGER.debug("GetOrdemServicoQuery: {}", query);

    var id = documentoWriteService.saveOrdemServico(query.getFuncionarioId(), null);

    return ResponseEntity.ok(id);
  }

}
