package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;

@Component
public class GetMobilidadeByIdQueryHandler implements QueryHandler<GetMobilidadeByIdQuery, ResponseEntity<MobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMobilidadeByIdQueryHandler.class);

  private final MobilidadeRepository mobilidadeRepository;
  private final MobilidadeMapper mobilidadeMapper;

  public GetMobilidadeByIdQueryHandler(MobilidadeRepository mobilidadeRepository, MobilidadeMapper mobilidadeMapper) {

    this.mobilidadeRepository = mobilidadeRepository;
    this.mobilidadeMapper = mobilidadeMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<MobilidadeDTO> handle(GetMobilidadeByIdQuery query) {

     IdentificadorUnico id = IdentificadorUnico.from(query.getId());
     LOGGER.info("Handling GetMobilidadeByIdQuery: {}", query);

     var mobilidade = mobilidadeRepository.getMobilidadeById(id).orElseThrow(
         () -> IgrpResponseStatusException.notFound("mobilidade nao encontrada com id"+query.getId())
     );
    return ResponseEntity.ok(mobilidadeMapper.mobilidadeDTO(mobilidade));
  }

}
