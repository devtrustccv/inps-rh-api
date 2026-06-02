package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.services.SaldoFeriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

    final Map<String, Object> response = new HashMap<>();
    response.put("funcionarioUuid", query.getFuncionarioId());
    response.put("anoReferencia", query.getAno());
    response.put("saldo", saldo);

    return ResponseEntity.ok(response);
  }

}
