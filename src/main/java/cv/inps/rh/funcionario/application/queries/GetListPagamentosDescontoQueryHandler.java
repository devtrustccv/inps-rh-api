package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.repository.DefinicaoPagamentoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;

import java.util.List;

@Component
public class GetListPagamentosDescontoQueryHandler implements QueryHandler<GetListPagamentosDescontoQuery, ResponseEntity<WrapperListPagamentosDescontoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListPagamentosDescontoQueryHandler.class);

  private final DefinicaoPagamentoRepository definicaoPagamentoRepository;
  private final DefPagamentoMapper definicaoPagamentoMapper;

  public GetListPagamentosDescontoQueryHandler(DefinicaoPagamentoRepository definicaoPagamentoRepository, DefPagamentoMapper definicaoPagamentoMapper) {

    this.definicaoPagamentoRepository = definicaoPagamentoRepository;
    this.definicaoPagamentoMapper = definicaoPagamentoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListPagamentosDescontoDTO> handle(GetListPagamentosDescontoQuery query) {
     LOGGER.info("Handling GetListPagamentosDescontoQuery: {}", query);

     var filter = definicaoPagamentoMapper.toFilterDomain(
         query.getEstado(),
         query.getDataInicio(),
         query.getDataFim(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );


     List<DefPagamento> pagamentosDescontos = definicaoPagamentoRepository.findAll(filter);

     var content = pagamentosDescontos.stream()
         .map(definicaoPagamentoMapper::toDTO)
         .toList();

     long totalElements = content.size();
     int pageNumber = filter.getPageNumber() != null ? filter.getPageNumber() : 0;
     int pageSize = filter.getPageSize() != null ? filter.getPageSize() : 20;
     int totalPages = (int) Math.ceil((double) totalElements / pageSize);


     var wrapper = new WrapperListPagamentosDescontoDTO();
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
