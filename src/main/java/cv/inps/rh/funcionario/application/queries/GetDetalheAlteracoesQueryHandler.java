package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.service.ValidacaoDetalheReadService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDetalheAlteracoesQueryHandler implements QueryHandler<GetDetalheAlteracoesQuery, ResponseEntity<List<ValidacaoDetalheDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheAlteracoesQueryHandler.class);

  private final ValidacaoDetalheReadService validacaoDetalheReadService;

  public GetDetalheAlteracoesQueryHandler(ValidacaoDetalheReadService validacaoDetalheReadService) {
    this.validacaoDetalheReadService = validacaoDetalheReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ValidacaoDetalheDTO>> handle(GetDetalheAlteracoesQuery query) {
    LOGGER.info("Handling GetDetalheAlteracoesQuery: {}", query);

    var idValidacao = IdentificadorUnico.from(query.getIdValidacao());

    return ResponseEntity.ok(validacaoDetalheReadService.listar(idValidacao.valor()));
  }

}
