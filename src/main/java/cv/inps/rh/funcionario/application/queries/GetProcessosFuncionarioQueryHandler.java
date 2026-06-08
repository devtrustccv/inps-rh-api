package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.funcionario.application.service.processodisciplinar.ProcessoDisciplinarReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetProcessosFuncionarioQueryHandler implements QueryHandler<GetProcessosFuncionarioQuery, ResponseEntity<List<ProcessoDisciplinarResponseDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessosFuncionarioQueryHandler.class);

  private final ProcessoDisciplinarReadService processoDisciplinarReadService;

  public GetProcessosFuncionarioQueryHandler(ProcessoDisciplinarReadService processoDisciplinarReadService) {
    this.processoDisciplinarReadService = processoDisciplinarReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ProcessoDisciplinarResponseDTO>> handle(GetProcessosFuncionarioQuery query) {

    var data = processoDisciplinarReadService.getProcessosDisciplinares(query.getFuncionarioId(), query.isValidacao());

    return ResponseEntity.ok(data);
  }

}
