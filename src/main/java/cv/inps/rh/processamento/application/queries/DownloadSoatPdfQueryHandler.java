package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class DownloadSoatPdfQueryHandler
    implements QueryHandler<DownloadSoatPdfQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadSoatPdfQueryHandler.class);

  private final SoatService service;

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(DownloadSoatPdfQuery query) {

    LOGGER.debug("DownloadSoatPdfQuery: {}", query);

    var result = service.gerarFicheiroSoat(query.getSoatId(), query.getApoliceId());

    var disposition = ContentDisposition.attachment()
        .filename(result.filename(), StandardCharsets.UTF_8)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(result.content().length)
        .body(result.content());
  }
}
