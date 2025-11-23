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
  private final FuncionarioRules funcionarioRoles;

  public GetContratoByIdQueryHandler(FuncionarioEntityRepository funcionarioEntityRepository, DadosContratuaisMapper dadosContratuaisMapper, FuncionarioRules funcionarioRoles) {

    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.dadosContratuaisMapper = dadosContratuaisMapper;
    this.funcionarioRoles = funcionarioRoles;
  }


  @Transactional(readOnly = true)
  @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    LOGGER.info("Handling GetContratoByIdQuery: {}", query);

    var contratoId = IdentificadorUnico.from(query.getContratoId()).getValor();

    var idFunc = IdentificadorUnico.from(query.getId());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

    var dadosContratuais = funcionarioRoles.getTipoRelacionamentoByContratoId(funcionario, contratoId);
    if (dadosContratuais == null)
      throw IgrpResponseStatusException.notFound("Contrato com id '%s' não encontrado".formatted(contratoId));

    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO2(dadosContratuais));
  }
}
