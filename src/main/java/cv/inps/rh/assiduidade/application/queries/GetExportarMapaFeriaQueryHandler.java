package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.services.MapaFeriaExcelExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GetExportarMapaFeriaQueryHandler
    implements QueryHandler<GetExportarMapaFeriaQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetExportarMapaFeriaQueryHandler.class);
  private static final MediaType EXCEL_TYPE =
      MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  private final MapaFeriaExcelExportService mapaFeriaExcelExportService;

  public GetExportarMapaFeriaQueryHandler(MapaFeriaExcelExportService mapaFeriaExcelExportService) {
    this.mapaFeriaExcelExportService = mapaFeriaExcelExportService;
  }

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(GetExportarMapaFeriaQuery query) {

    LOGGER.debug("GetExportarMapaFeriaQuery: {}", query);

    byte[] excel = mapaFeriaExcelExportService.exportarMapaFeria(query);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(EXCEL_TYPE);
    headers.setContentDisposition(
        ContentDisposition.attachment().filename("mapa-ferias.xlsx").build());
    headers.setContentLength(excel.length);

    return ResponseEntity.ok().headers(headers).body(excel);
  }

}
