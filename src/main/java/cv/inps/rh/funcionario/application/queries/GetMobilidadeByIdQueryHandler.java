package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.service.MobilidadeReadService;
import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMobilidadeByIdQueryHandler implements QueryHandler<GetMobilidadeByIdQuery, ResponseEntity<MobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMobilidadeByIdQueryHandler.class);

  private final MobilidadeRepository mobilidadeRepository;
  private final MobilidadeMapper mobilidadeMapper;

  private final MobilidadeReadService mobilidadeReadService
      ;
  public GetMobilidadeByIdQueryHandler(MobilidadeRepository mobilidadeRepository, MobilidadeMapper mobilidadeMapper, MobilidadeReadService mobilidadeReadService) {

    this.mobilidadeRepository = mobilidadeRepository;
    this.mobilidadeMapper = mobilidadeMapper;
    this.mobilidadeReadService = mobilidadeReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<MobilidadeDTO> handle(GetMobilidadeByIdQuery query) {


     LOGGER.info("Handling GetMobilidadeByIdQuery: {}", query);
     return ResponseEntity.ok(mobilidadeReadService.getMobilidade(query));
    /* IdentificadorUnico id = IdentificadorUnico.from(query.getId());
     var mobilidade = mobilidadeRepository.getMobilidadeById(id).orElseThrow(
         () -> IgrpResponseStatusException.notFound("mobilidade nao encontrada com id"+query.getId())
     );
    return ResponseEntity.ok(mobilidadeMapper.mobilidadeDTO(mobilidade));*/
  }

}
