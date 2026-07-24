package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;

  public GetContratoByIdQueryHandler(FuncionarioEntityRepository funcionarioEntityRepository, DadosContratuaisMapper dadosContratuaisMapper, FuncionarioRules funcionarioRules) {

    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.dadosContratuaisMapper = dadosContratuaisMapper;
    this.funcionarioRules = funcionarioRules;
  }


  @Transactional(readOnly = true)
  @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    LOGGER.info("Handling GetContratoByIdQuery: {}", query);

    var contratoId = IdentificadorUnico.from(query.getContratoId()).valor();

    var idFunc = IdentificadorUnico.from(query.getId());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoByContratoId(funcionario.getUuid(), contratoId);
    if (tiposRelacionamento == null)
      throw IgrpResponseStatusException.notFound("Contrato com id '%s' não encontrado".formatted(contratoId));

    // Def SÓ deste contrato e vigentes: pela associação do tiprel, filtradas pelo estado do próprio
    // tiprel (mesmo padrão do CarreiraReadService). Sem isto, def eliminados/inactivos (E/I) que
    // continuam associados ao tiprel — ex.: manuais substituídos pelo sync na validação — apareciam
    // no detalhe. O def não tem coluna de contrato; a associação + estado do tiprel é o vínculo.
    var estadoTiprel = tiposRelacionamento.getEstado();
    var remuneracoes = funcionarioRules.getRemuneracoesAssociados(tiposRelacionamento.getId())
        .stream().filter(r -> r.getEstado() == estadoTiprel).toList();
    var pagamentos = funcionarioRules.getPagamentosDescontosAssociados(tiposRelacionamento.getId())
        .stream().filter(p -> p.getEstado() == estadoTiprel).toList();

    var dadosContratuaisResp = dadosContratuaisMapper
        .dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes);

    return ResponseEntity.ok(dadosContratuaisResp);
  }
}
