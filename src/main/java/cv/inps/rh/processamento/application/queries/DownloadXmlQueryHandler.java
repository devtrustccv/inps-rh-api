package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.XmlFosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class DownloadXmlQueryHandler implements QueryHandler<DownloadXmlQuery, ResponseEntity<byte[]>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadXmlQueryHandler.class);

  private final XmlFosService xmlFosService;

  public DownloadXmlQueryHandler(XmlFosService xmlFosService) {
    this.xmlFosService = xmlFosService;
  }

  @IgrpQueryHandler
  public ResponseEntity<byte[]> handle(DownloadXmlQuery query) {

    LOGGER.debug("DownloadXmlQuery: {}", query);

    var xml = xmlFosService.buildXml(query.getFosId()).getBytes();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fosXml.xml")
        .contentType(MediaType.APPLICATION_XML)
        .contentLength(xml.length)
        .body(xml);
  }

}
