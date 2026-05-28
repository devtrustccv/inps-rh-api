package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.BaixaMedicaCalculoDTO;
import cv.inps.rh.processamento.domain.service.baixamedica.BaixaMedicaServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class GetCalculoBaixaMedicaQueryHandler
    implements QueryHandler<GetCalculoBaixaMedicaQuery, ResponseEntity<BaixaMedicaCalculoDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCalculoBaixaMedicaQueryHandler.class);

  private final BaixaMedicaServiceWrite baixaMedicaServiceWrite;

  public GetCalculoBaixaMedicaQueryHandler(BaixaMedicaServiceWrite baixaMedicaServiceWrite) {
    this.baixaMedicaServiceWrite = baixaMedicaServiceWrite;
  }

  @IgrpQueryHandler
  public ResponseEntity<BaixaMedicaCalculoDTO> handle(GetCalculoBaixaMedicaQuery query) {

    LOGGER.debug("GetCalculoBaixaMedicaQuery: {}", query);

    var resultado = baixaMedicaServiceWrite.chamarProcedure(
        baixaMedicaServiceWrite.getTiprelId(query.getColaborador()),
        LocalDate.parse(query.getDataInicio()),
        LocalDate.parse(query.getDataFim()),
        query.getTipoLicenca(),
        query.getDataInicioFalta() != null ? LocalDate.parse(query.getDataInicioFalta()) : null);

    return ResponseEntity.ok(resultado);
  }

}
