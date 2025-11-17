package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.domain.repository.ContratoRepository;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);

  private final ContratoRepository contratoRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final ContratoMapper contratoMapper;

  public GetContratoByIdQueryHandler(ContratoRepository contratoRepository, FuncionarioRepository funcionarioRepository, ContratoMapper contratoMapper) {
    this.contratoRepository = contratoRepository;
    this.funcionarioRepository = funcionarioRepository;
    this.contratoMapper = contratoMapper;
  }

  @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    LOGGER.info("Handling GetContratoByIdQuery: {}", query);
    Long id;
    try {
      id = Long.parseLong(query.getId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("id de contrato invalido: " + query.getId());
    }

    var contrato = contratoRepository.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("contrato nao encontrado com id" + query.getId()));

    var func = funcionarioRepository.findById(cv.inps.rh.shared.domain.models.IdentificadorUnico.from(contrato.getUuidFuncionario()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com contrato id" + query.getId()));
    var tr = func.getTipoRelacionamentoByContratoId(contrato.getUuid());
    var resp = contratoMapper.toRespDTO(contrato, tr);
    return ResponseEntity.ok(resp);
  }
}
