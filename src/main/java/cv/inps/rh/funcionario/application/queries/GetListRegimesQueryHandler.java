package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.funcionario.domain.repository.RegimeRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;

import java.util.List;

@Component
public class GetListRegimesQueryHandler implements QueryHandler<GetListRegimesQuery, ResponseEntity<WrapperRegimeListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRegimesQueryHandler.class);

  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final RegimeRepository regimeRepository;

  public GetListRegimesQueryHandler(RegimeTrabalhoMapper regimeTrabalhoMapper, RegimeRepository regimeRepository) {

    this.regimeTrabalhoMapper = regimeTrabalhoMapper;
    this.regimeRepository = regimeRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperRegimeListDTO> handle(GetListRegimesQuery query) {

    LOGGER.info("Handling GetListRegimesQuery: {}", query);

     var filter = regimeTrabalhoMapper.toFilterDomain(
         query.getTipoRegime(),
         query.getEstado(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );


     List<RegimeTrabalho> regimes = regimeRepository.findAll(filter);


     var content = regimes.stream()
         .map(regimeTrabalhoMapper::toDTO)
         .toList();

     long totalElements = content.size();
     int pageNumber = filter.getPageNumber() != null ? filter.getPageNumber() : 0;
     int pageSize = filter.getPageSize() != null ? filter.getPageSize() : 20;
     int totalPages = (int) Math.ceil((double) totalElements / pageSize);


     var wrapper = new WrapperRegimeListDTO();
     wrapper.setContent(content);
     wrapper.setPageNumber(pageNumber);
     wrapper.setPageSize(pageSize);
     wrapper.setTotalElements(totalElements);
     wrapper.setTotalPages(totalPages);
     wrapper.setFirst(pageNumber == 0);
     wrapper.setLast(pageNumber + 1 >= totalPages);

     return ResponseEntity.ok(wrapper);
  }

}
