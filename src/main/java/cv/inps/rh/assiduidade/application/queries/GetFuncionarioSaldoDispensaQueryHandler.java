package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.HorasDispensaStatusDTO;
import cv.inps.rh.assiduidade.application.services.DispensaHorasService;
import cv.inps.rh.shared.util.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetFuncionarioSaldoDispensaQueryHandler implements QueryHandler<GetFuncionarioSaldoDispensaQuery, ResponseEntity<HorasDispensaStatusDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncionarioSaldoDispensaQueryHandler.class);


  private final DispensaHorasService dispensaHorasService;
  public GetFuncionarioSaldoDispensaQueryHandler(DispensaHorasService dispensaHorasService) {

    this.dispensaHorasService = dispensaHorasService;
  }

   @IgrpQueryHandler
  public ResponseEntity<HorasDispensaStatusDTO> handle(GetFuncionarioSaldoDispensaQuery query) {

    LOGGER.debug("GetFuncionarioSaldoDispensaQuery: {}", query);

    var funcUuid = UUID.fromString(query.getFuncionarioId());
    var dataRef = DateFormatter.stringToLocalDate(query.getData());

    return ResponseEntity.ok(dispensaHorasService.getHorasStatus(funcUuid,dataRef
        ));
  }

}
