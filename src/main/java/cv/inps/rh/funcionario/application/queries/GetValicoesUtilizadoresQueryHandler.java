package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.domain.repository.ValidacaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ValidacaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;

@Component
public class GetValicoesUtilizadoresQueryHandler implements QueryHandler<GetValicoesUtilizadoresQuery, ResponseEntity<WrapperListaValidacoesDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetValicoesUtilizadoresQueryHandler.class);

  private final ValidacaoRepository validacaoRepository;
  private final ValidacaoMapper validacaoMapper;

  public GetValicoesUtilizadoresQueryHandler(ValidacaoRepository validacaoRepository, ValidacaoMapper validacaoMapper) {

    this.validacaoRepository = validacaoRepository;
    this.validacaoMapper = validacaoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaValidacoesDTO> handle(GetValicoesUtilizadoresQuery query) {
     LOGGER.info("Handling GetValicoesUtilizadoresQuery: {}", query);

     // Converter query em filtro de domínio
     var filters = validacaoMapper.toFilterDomain(
         query.getNomeColaborador(),
         query.getTipoOperacao(),
         query.getReferenciaName(),
         query.getDataInicio(),
         query.getDataFim(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );

     // Buscar lista de validações
     var validacoes = validacaoRepository.findAll(filters);

     // Mapear para DTO
     var content = validacoes.stream()
         .map(validacaoMapper::toDto)
         .toList();

     // Paginação
     long totalElements = content.size();
     int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
     int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 20;
     int totalPages = (int) Math.ceil((double) totalElements / pageSize);

     // Montar wrapper DTO
     var wrapper = new WrapperListaValidacoesDTO();
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
