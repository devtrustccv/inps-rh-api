package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.service.ContratoReadService;
import cv.inps.rh.funcionario.domain.repository.ContratoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;

@Component
public class GetListContratosQueryHandler implements QueryHandler<GetListContratosQuery, ResponseEntity<WrapperListContratoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListContratosQueryHandler.class);

  private final ContratoRepository contratoRepository;
  private final ContratoMapper contratoMapper;

  private final ContratoReadService contratoReadService;

  public GetListContratosQueryHandler(ContratoRepository contratoRepository, ContratoMapper contratoMapper, ContratoReadService contratoReadService) {

    this.contratoRepository = contratoRepository;
    this.contratoMapper = contratoMapper;
    this.contratoReadService = contratoReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListContratoDTO> handle(GetListContratosQuery query) {
    // TODO: Implement the query handling logic here
     // Paginação

     LOGGER.info("Handling GetListContratosQuery: {}", query);

     return ResponseEntity.ok(contratoReadService.listaContratos(query));

    /* var filters = contratoMapper.toFilterDomain(
         query.getVinculo(),
         query.getIdFuncionario(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );

     var contratos = contratoRepository.findAll(filters);

     // Mapear para DTO
     var content = contratos.stream()
         .map(contratoMapper::toDTO)
         .toList();

     long totalElements = content.size();
     int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
     int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 20;
     int totalPages = (int) Math.ceil((double) totalElements / pageSize);

     // Montar wrapper DTO
     var wrapper = new WrapperListContratoDTO();
     wrapper.setContent(content);
     wrapper.setPageNumber(pageNumber);
     wrapper.setPageSize(pageSize);
     wrapper.setTotalElements(totalElements);
     wrapper.setTotalPages(totalPages);
     wrapper.setFirst(pageNumber == 0);
     wrapper.setLast(pageNumber + 1 >= totalPages);

     return ResponseEntity.ok(wrapper);*/
  }

}
