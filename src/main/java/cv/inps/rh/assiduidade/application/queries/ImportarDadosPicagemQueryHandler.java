package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.services.PicagemImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ImportarDadosPicagemQueryHandler
    implements QueryHandler<ImportarDadosPicagemQuery, ResponseEntity<Map<String, Object>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportarDadosPicagemQueryHandler.class);

  private final PicagemImportService picagemImportService;

  public ImportarDadosPicagemQueryHandler(PicagemImportService picagemImportService) {
    this.picagemImportService = picagemImportService;
  }

  @IgrpQueryHandler
  public ResponseEntity<Map<String, Object>> handle(ImportarDadosPicagemQuery query) {

    LOGGER.debug("ImportarDadosPicagemQuery: {}", query);

    return ResponseEntity.ok(picagemImportService.importarDados(query));
  }

}
