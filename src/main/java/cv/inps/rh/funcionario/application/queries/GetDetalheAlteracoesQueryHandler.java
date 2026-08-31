package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.funcionario.application.service.historicolaboral.AlteracaoEscalaoDetalheReadService;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.JaversValidacaoDetalheReadService;
import cv.inps.rh.shared.application.service.ValidacaoDetalheReadService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetDetalheAlteracoesQueryHandler implements QueryHandler<GetDetalheAlteracoesQuery, ResponseEntity<List<ValidacaoDetalheDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDetalheAlteracoesQueryHandler.class);

  // Fonte da grelha "Detalhe de alterações" migrada da tabela RH_T_VALIDACAO_DETALHE para o histórico
  // do JaVers (piloto validado end-to-end na mobilidade). O path oficial
  // (GET .../validacoes/{id}/detalhes) e o ValidacaoDetalheDTO mantêm-se — o frontend não nota.
  // O ValidacaoDetalheReadService antigo (leitura da tabela) fica disponível para rollback rápido.
  private final JaversValidacaoDetalheReadService javersValidacaoDetalheReadService;
  // Exceção ALTERACAO_ESCALAO: o tiprel é Shallow Reference (JaVers grava-o vazio), por isso esta
  // referência é servida por um reader dedicado que compara o tiprel pendente com o predecessor. É a
  // ÚNICA referência desviada do JaVers — todas as outras continuam pelo caminho de cima, intactas.
  private final AlteracaoEscalaoDetalheReadService alteracaoEscalaoDetalheReadService;
  // Leitura da tabela RH_T_VALIDACAO_DETALHE (persistida na escrita pelo EscalaoDetalheDiffWriter).
  private final ValidacaoDetalheReadService validacaoDetalheReadService;
  private final ValidacaoDetalheEntityRepository validacaoDetalheEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  public GetDetalheAlteracoesQueryHandler(JaversValidacaoDetalheReadService javersValidacaoDetalheReadService,
      AlteracaoEscalaoDetalheReadService alteracaoEscalaoDetalheReadService,
      ValidacaoDetalheReadService validacaoDetalheReadService,
      ValidacaoDetalheEntityRepository validacaoDetalheEntityRepository,
      ValidacaoEntityRepository validacaoEntityRepository) {
    this.javersValidacaoDetalheReadService = javersValidacaoDetalheReadService;
    this.alteracaoEscalaoDetalheReadService = alteracaoEscalaoDetalheReadService;
    this.validacaoDetalheReadService = validacaoDetalheReadService;
    this.validacaoDetalheEntityRepository = validacaoDetalheEntityRepository;
    this.validacaoEntityRepository = validacaoEntityRepository;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ValidacaoDetalheDTO>> handle(GetDetalheAlteracoesQuery query) {
    LOGGER.info("Handling GetDetalheAlteracoesQuery: {}", query);

    var idValidacao = IdentificadorUnico.from(query.getIdValidacao());
    UUID validacaoUuid = idValidacao.valor();

    if (isAlteracaoEscalao(validacaoUuid)) {
      // Novo padrão: se a escrita já persistiu o detalhe em RH_T_VALIDACAO_DETALHE, lê da tabela (via
      // comum). Fallback on-the-fly só para movimentos antigos, sem linhas persistidas — zero regressão.
      if (validacaoDetalheEntityRepository.existsByValidacaoId_Uuid(validacaoUuid)) {
        return ResponseEntity.ok(validacaoDetalheReadService.listar(validacaoUuid));
      }
      return ResponseEntity.ok(alteracaoEscalaoDetalheReadService.listar(validacaoUuid));
    }
    return ResponseEntity.ok(javersValidacaoDetalheReadService.listar(validacaoUuid));
  }

  private boolean isAlteracaoEscalao(UUID validacaoUuid) {
    return validacaoEntityRepository.findByUuid(validacaoUuid)
        .map(v -> Referencia.ALTERACAO_ESCALAO.name().equals(v.getReferenciaName()))
        .orElse(false);
  }

}
