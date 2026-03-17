package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.configuracao.application.dto.EquipamentoListRequestDTO;
import cv.inps.rh.configuracao.application.services.EquipamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetEquipamentosLocalTrabalhoQueryHandler implements QueryHandler<GetEquipamentosLocalTrabalhoQuery, ResponseEntity<EquipamentoListRequestDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetEquipamentosLocalTrabalhoQueryHandler.class);

  private final EquipamentoService equipamentoService;

  public GetEquipamentosLocalTrabalhoQueryHandler(EquipamentoService equipamentoService) {
    this.equipamentoService = equipamentoService;
  }

  @IgrpQueryHandler
  public ResponseEntity<EquipamentoListRequestDTO> handle(GetEquipamentosLocalTrabalhoQuery query) {

    LOGGER.debug("GetEquipamentosLocalTrabalhoQuery: {}", query);

    var data = equipamentoService.getEquipmentsByLocalId(query.getLocalTrabalhoId());

    return ResponseEntity.ok(data);
  }

}
