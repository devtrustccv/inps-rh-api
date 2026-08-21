package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.service.JaversValidacaoDetalheReadService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDetalheAlteracoesQueryHandler implements QueryHandler<GetDetalheAlteracoesQuery, ResponseEntity<List<ValidacaoDetalheDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheAlteracoesQueryHandler.class);

  // Fonte da grelha "Detalhe de alterações" migrada da tabela RH_T_VALIDACAO_DETALHE para o histórico
  // do JaVers (piloto validado end-to-end na mobilidade). O path oficial
  // (GET .../validacoes/{id}/detalhes) e o ValidacaoDetalheDTO mantêm-se — o frontend não nota.
  // O ValidacaoDetalheReadService antigo (leitura da tabela) fica disponível para rollback rápido.
  private final JaversValidacaoDetalheReadService javersValidacaoDetalheReadService;

  public GetDetalheAlteracoesQueryHandler(JaversValidacaoDetalheReadService javersValidacaoDetalheReadService) {
    this.javersValidacaoDetalheReadService = javersValidacaoDetalheReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ValidacaoDetalheDTO>> handle(GetDetalheAlteracoesQuery query) {
    LOGGER.info("Handling GetDetalheAlteracoesQuery: {}", query);

    var idValidacao = IdentificadorUnico.from(query.getIdValidacao());

    return ResponseEntity.ok(javersValidacaoDetalheReadService.listar(idValidacao.valor()));
  }

}
