package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetFuncionarioByIdQueryHandler implements QueryHandler<GetFuncionarioByIdQuery, ResponseEntity<FuncionarioResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncionarioByIdQueryHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final FuncionarioMapper funcionarioMapper;

  public GetFuncionarioByIdQueryHandler(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {

    this.funcionarioRepository = funcionarioRepository;
    this.funcionarioMapper = funcionarioMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<FuncionarioResponseDTO> handle(GetFuncionarioByIdQuery query) {
    // TODO: Implement the query handling logic here
     var funcionario = funcionarioRepository.findById(IdentificadorUnico.from(query.getId()))
         .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id"+query.getId()));

     return ResponseEntity.ok(funcionarioMapper.toResponse2DTO(funcionario));

  }

}
