package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.assiduidade.application.services.SaldoFeriaService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class GetFuncioarioSaldoFeriasQueryHandler
    implements QueryHandler<GetFuncioarioSaldoFeriasQuery, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncioarioSaldoFeriasQueryHandler.class);


  private final SaldoFeriaService saldoFeriaService;


  public GetFuncioarioSaldoFeriasQueryHandler(SaldoFeriaService saldoFeriaService)    {
    this.saldoFeriaService = saldoFeriaService;
  }

  @IgrpQueryHandler
  public ResponseEntity<Map<String, ?>> handle(GetFuncioarioSaldoFeriasQuery query) {

    LOGGER.debug("GetFuncioarioSaldoFeriasQuery: {}", query);

    var uuidFunc = UUID.fromString(query.getFuncionarioId());

   var saldo = saldoFeriaService.getSaldo(uuidFunc, query.getAno());

    final Map<String, Object> response = Map.of(
        "funcionarioUuid", query.getFuncionarioId(),
        "anoReferencia", query.getAno(),
        "saldo", saldo);

    return ResponseEntity.ok(response);
  }

}
