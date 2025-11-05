package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.domain.filters.MobilidadeFilters;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.funcionario.domain.repository.MobilidadeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;

import java.util.List;

@Component
public class GetListMobilidadesQueryHandler implements QueryHandler<GetListMobilidadesQuery, ResponseEntity<WrapperListMobilidadeDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListMobilidadesQueryHandler.class);

  private final MobilidadeRepository mobilidadeRepository;
  private final MobilidadeMapper mobilidadeMapper;

  public GetListMobilidadesQueryHandler(MobilidadeRepository mobilidadeRepository, MobilidadeMapper mobilidadeMapper) {

    this.mobilidadeRepository = mobilidadeRepository;
    this.mobilidadeMapper = mobilidadeMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListMobilidadeDTO> handle(GetListMobilidadesQuery query) {
     LOGGER.info("Handling GetListMobilidadesQuery: {}", query);

     // Converte os parâmetros da query para o domain filter
     MobilidadeFilters filters = mobilidadeMapper.toFilterDomain(
         query.getTipoMobilidade(),
         query.getDataInicio(),
         query.getDataFim(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );

     int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
     int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 50;
     int startRow = pageNumber * pageSize + 1;
     int endRow = (pageNumber + 1) * pageSize;

     // Chama o repository com a query nativa
     List<MobilidadeList> mobilidades = mobilidadeRepository.findAll(filters);

     long totalElements = mobilidades.isEmpty() ? 0 : mobilidades.getFirst().getTotalCount();

     List<MobilidadeListDTO> content = mobilidades.stream()
         .map(mobilidadeMapper::mobilidadeListDTO)
         .toList();

     var wrapper = new WrapperListMobilidadeDTO();
     wrapper.setContent(content);
     wrapper.setPageNumber(filters.getPageNumber());
     wrapper.setPageSize(filters.getPageSize());
     wrapper.setTotalElements(totalElements);
     wrapper.setTotalPages((int) Math.ceil((double) totalElements / filters.getPageSize()));
     wrapper.setFirst(filters.getPageNumber() == 0);
     wrapper.setLast(filters.getPageNumber() + 1 >= wrapper.getTotalPages());

     return ResponseEntity.ok(wrapper);

  }

}
