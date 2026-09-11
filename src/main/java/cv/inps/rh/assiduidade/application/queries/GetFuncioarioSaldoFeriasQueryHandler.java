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

    var detalhe = saldoFeriaService.detalhe(uuidFunc, query.getAno(), null);

    final Map<String, Object> response = new HashMap<>();
    response.put("funcionarioUuid", query.getFuncionarioId());
    // Nulo quando nao se pede ano: o calculo e entao o acumulado de todos os anos. O `ambito`
    // diz qual dos dois foi feito, em vez de deixar o ecra a adivinhar porque veio nulo.
    response.put("anoReferencia", detalhe.anoReferencia());
    response.put("ambito", detalhe.ambito());
    // Parcelas: um `saldo: 0` sozinho nao distinguia "nao tem direito" de "ja gastou tudo".
    response.put("direito", detalhe.direito());
    response.put("gozado", detalhe.gozado());
    response.put("reservado", detalhe.reservado());
    response.put("saldo", detalhe.saldo());

    return ResponseEntity.ok(response);
  }

}
