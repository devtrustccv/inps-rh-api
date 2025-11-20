package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetFuncionarioByIdQueryHandler implements QueryHandler<GetFuncionarioByIdQuery, ResponseEntity<FuncionarioResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncionarioByIdQueryHandler.class);

  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public GetFuncionarioByIdQueryHandler(FuncionarioMapper funcionarioMapper, FuncionarioEntityRepository funcionarioEntityRepository) {

    this.funcionarioMapper = funcionarioMapper;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
  }

   @Transactional(readOnly = true)
   @IgrpQueryHandler
  public ResponseEntity<FuncionarioResponseDTO> handle(GetFuncionarioByIdQuery query) {
    // TODO: Implement the query handling logic here
     var funcionario = funcionarioEntityRepository.findByUuid(IdentificadorUnico.from(query.getId()).getValor())
         .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id"+query.getId()));

     return ResponseEntity.ok(funcionarioMapper.toResponseDTO(funcionario));

  }

}
