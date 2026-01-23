package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetFuncionarioByIdQueryHandler implements QueryHandler<GetFuncionarioByIdQuery, ResponseEntity<FuncionarioResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncionarioByIdQueryHandler.class);

  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;

  public GetFuncionarioByIdQueryHandler(FuncionarioMapper funcionarioMapper, FuncionarioEntityRepository funcionarioEntityRepository, DadosContratuaisMapper dadosContratuaisMapper, FuncionarioRules funcionarioRules) {

    this.funcionarioMapper = funcionarioMapper;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.dadosContratuaisMapper = dadosContratuaisMapper;
    this.funcionarioRules = funcionarioRules;
  }

  @Transactional(readOnly = true)
  @IgrpQueryHandler
  public ResponseEntity<FuncionarioResponseDTO> handle(GetFuncionarioByIdQuery query) {

    LOGGER.info("Handling GetFuncionarioByIdQuery: {}", query);

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getId()).valor());

    var funcionarioResponseDTO = funcionarioMapper.toResponseDTO(funcionario);

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId());

    var dcr = dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos,
        remuneracoes);
    funcionarioResponseDTO.setDadosContratuais(dcr);


    return ResponseEntity.ok(funcionarioResponseDTO);


  }

}
