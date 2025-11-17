package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.domain.repository.ContratoRepository;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.domain.repository.TipoRelacionamentoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final ContratoRepository contratoRepository;
  private final ContratoMapper contratoMapper;

  public GetContratoByIdQueryHandler(FuncionarioRepository funcionarioRepository, ContratoRepository contratoRepository, ContratoMapper contratoMapper) {
    this.funcionarioRepository = funcionarioRepository;
    this.contratoRepository = contratoRepository;
    this.contratoMapper = contratoMapper;
  }


  @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    LOGGER.info("Handling GetContratoByIdQuery: {}", query);
    var contratoId = IdentificadorUnico.from(query.getContratoId());

    var funcionarioId = IdentificadorUnico.from(query.getId());

    var funcionario = funcionarioRepository.findById(funcionarioId).orElseThrow(
                () -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + query.getId())
    );

    var tipoRelacionamento = funcionario.getTipoRelacionamentoByContratoId(contratoId);

    var dto = contratoMapper.toRespDTO(tipoRelacionamento);

    return ResponseEntity.ok(dto);


  }
}
