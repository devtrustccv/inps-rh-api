package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);

  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRepository funcionarioRepository;

  public GetContratoByIdQueryHandler(FuncionarioMapper funcionarioMapper, FuncionarioRepository funcionarioRepository) {

    this.funcionarioMapper = funcionarioMapper;
    this.funcionarioRepository = funcionarioRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {

     var idFuncionario = IdentificadorUnico.from(query.getId());

     var funcionario = funcionarioRepository.findById(idFuncionario).orElseThrow();

     return null;
  }

}
