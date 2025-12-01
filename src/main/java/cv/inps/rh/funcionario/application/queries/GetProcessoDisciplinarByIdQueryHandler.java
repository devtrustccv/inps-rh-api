package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.funcionario.application.service.processodisciplinar.ProcessoDisciplinarReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessoDisciplinarByIdQueryHandler implements QueryHandler<GetProcessoDisciplinarByIdQuery, ResponseEntity<ProcessoDisciplinarResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessoDisciplinarByIdQueryHandler.class);

  private final ProcessoDisciplinarReadService processoDisciplinarReadService;

  public GetProcessoDisciplinarByIdQueryHandler(ProcessoDisciplinarReadService processoDisciplinarReadService) {
    this.processoDisciplinarReadService = processoDisciplinarReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<ProcessoDisciplinarResponseDTO> handle(GetProcessoDisciplinarByIdQuery query) {

    LOGGER.debug("Handling GetProcessoDisciplinarByIdQuery: {}", query);

    var data = processoDisciplinarReadService.getProcessoDisciplinarById(query.getProcessoDisciplinarId());

    return ResponseEntity.ok(data);
  }

}
