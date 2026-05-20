package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.SubsidioFeriasResponseDTO;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
public class GetSubsidioFeriasQueryHandler implements QueryHandler<GetSubsidioFeriasQuery, ResponseEntity<List<SubsidioFeriasResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubsidioFeriasQueryHandler.class);

  private final SubsidioService subsidioService;

  public GetSubsidioFeriasQueryHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<SubsidioFeriasResponseDTO>> handle(GetSubsidioFeriasQuery query) {

    LOGGER.debug("GetSubsidioFeriasQuery: {}", query);

    var dataProcessamento = getDate(query.getDataProcessamento());

    var result = subsidioService.getSubsidioFeriasData(
        dataProcessamento,
        dataProcessamento.toLocalDate().getYear(),
        query.getDirecaoId(),
        query.getFuncionarioId()
    );

    return ResponseEntity.ok(result);
  }

  private Date getDate(String dataProcessamento) {

    if (StringUtils.hasText(dataProcessamento))
      return Date.valueOf(LocalDate.parse(dataProcessamento));

    return Date.valueOf(LocalDate.now());
  }

}
