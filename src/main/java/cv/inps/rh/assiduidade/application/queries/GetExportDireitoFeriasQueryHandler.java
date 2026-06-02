package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.services.FeriaExcelExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GetExportDireitoFeriasQueryHandler
    implements QueryHandler<GetExportDireitoFeriasQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetExportDireitoFeriasQueryHandler.class);
  private static final MediaType EXCEL_TYPE =
      MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  private final FeriaExcelExportService feriaExcelExportService;

  public GetExportDireitoFeriasQueryHandler(FeriaExcelExportService feriaExcelExportService) {
    this.feriaExcelExportService = feriaExcelExportService;
  }

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(GetExportDireitoFeriasQuery query) {

    LOGGER.debug("GetExportDireitoFeriasQuery: {}", query);

    byte[] excel = feriaExcelExportService.exportDireitoFerias(query);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(EXCEL_TYPE);
    headers.setContentDisposition(
        ContentDisposition.attachment().filename("direito-ferias.xlsx").build());
    headers.setContentLength(excel.length);

    return ResponseEntity.ok().headers(headers).body(excel);
  }

}
