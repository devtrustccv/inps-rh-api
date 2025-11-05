package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.funcionario.domain.repository.CarreiraRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;

import java.util.List;

@Component
public class GetCarreiraListQueryHandler implements QueryHandler<GetCarreiraListQuery, ResponseEntity<WrapperCarreiraListDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCarreiraListQueryHandler.class);

  private final CarreiraRepository carreiraRepository;
  private final CarreiraMapper carreiraMapper;

  public GetCarreiraListQueryHandler(CarreiraRepository carreiraRepository, CarreiraMapper carreiraMapper) {

    this.carreiraRepository = carreiraRepository;
    this.carreiraMapper = carreiraMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperCarreiraListDTO> handle(GetCarreiraListQuery query) {
     var filter = carreiraMapper.toFilterDomain(
         query.getTipoCarreira(),
         query.getDataInicio(),
         query.getDataFim(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );

     List<CarreiraList> list = carreiraRepository.findAll(filter);

     long total = list.isEmpty() ? 0 : list.get(0).getTotalCount();

     List<CarreiraListDTO> content = list.stream()
         .map(carreiraMapper::toDTO)
         .toList();

     var wrapper = new WrapperCarreiraListDTO();
     wrapper.setContent(content);
     wrapper.setPageNumber(filter.getPageNumber());
     wrapper.setPageSize(filter.getPageSize());
     wrapper.setTotalElements(total);
     wrapper.setTotalPages((int) Math.ceil((double) total / filter.getPageSize()));
     wrapper.setFirst(filter.getPageNumber() == 0);
     wrapper.setLast(filter.getPageNumber() + 1 >= wrapper.getTotalPages());

     return ResponseEntity.ok(wrapper);
  }

}
