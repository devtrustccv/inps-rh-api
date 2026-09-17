package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.service.ValidacaoDetalheReadService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Grelha "Detalhe de alterações" de uma validação. Uma só fonte: {@code RH_T_VALIDACAO_DETALHE},
 * congelada na escrita por cada módulo (ver {@code DetalheAlteracoes}). As validações anteriores à
 * migração foram copiadas para a mesma tabela antes de o histórico do JaVers ser largado.
 */
@Component
@RequiredArgsConstructor
public class GetDetalheAlteracoesQueryHandler implements QueryHandler<GetDetalheAlteracoesQuery, ResponseEntity<List<ValidacaoDetalheDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheAlteracoesQueryHandler.class);

  private final ValidacaoDetalheReadService validacaoDetalheReadService;

  @IgrpQueryHandler
  public ResponseEntity<List<ValidacaoDetalheDTO>> handle(GetDetalheAlteracoesQuery query) {
    LOGGER.info("Handling GetDetalheAlteracoesQuery: {}", query);
    var validacaoUuid = IdentificadorUnico.from(query.getIdValidacao()).valor();
    return ResponseEntity.ok(validacaoDetalheReadService.listar(validacaoUuid));
  }
}
