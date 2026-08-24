package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.emprestimo.domain.service.process.AquisicaoViaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SaveDecisaoAnaliseQueryHandler implements QueryHandler<SaveDecisaoAnaliseQuery, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseQueryHandler.class);

  private final AquisicaoViaturaService pedidoAquisicaoViaturaService;

  public SaveDecisaoAnaliseQueryHandler(AquisicaoViaturaService pedidoAquisicaoViaturaService) {
    this.pedidoAquisicaoViaturaService = pedidoAquisicaoViaturaService;
  }

  @IgrpQueryHandler
  public ResponseEntity<String> handle(SaveDecisaoAnaliseQuery query) {

    LOGGER.debug("SaveDecisaoAnaliseQuery: {}", query);

    pedidoAquisicaoViaturaService.saveUpdateDecisaoAnaliseRh(query.getEmprestimoId(), query.getAnaliserhrequest());

    return ResponseEntity.ok().build();
  }

}
