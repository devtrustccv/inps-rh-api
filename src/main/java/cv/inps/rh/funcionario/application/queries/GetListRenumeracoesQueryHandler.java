package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.service.RenumeracoesReadService;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.funcionario.domain.repository.DefinicaoRenumeracaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetListRenumeracoesQueryHandler implements QueryHandler<GetListRenumeracoesQuery, ResponseEntity<WrapperListRenumeracaoDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListRenumeracoesQueryHandler.class);

  private final DefinicaoRenumeracaoRepository definicaoRenumeracaoRepository;
  private final DefinicaoRemuneracaoMapper definicaoRenumeracaoMapper;

  private final RenumeracoesReadService renumeracoesReadService;

  public GetListRenumeracoesQueryHandler(DefinicaoRenumeracaoRepository definicaoRenumeracaoRepository, DefinicaoRemuneracaoMapper definicaoRenumeracaoMapper, RenumeracoesReadService renumeracoesReadService) {

    this.definicaoRenumeracaoRepository = definicaoRenumeracaoRepository;
    this.definicaoRenumeracaoMapper = definicaoRenumeracaoMapper;
    this.renumeracoesReadService = renumeracoesReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListRenumeracaoDTO> handle(GetListRenumeracoesQuery query) {

    LOGGER.info("Handling GetListRenumeracoesQuery: {}", query);

    return ResponseEntity.ok(renumeracoesReadService.getListRenumeracoes(query));

     /*var filter = definicaoRenumeracaoMapper.toFilterDomain(
         query.getEstado(),
         query.getDataInicio(),
         query.getDataFim(),
         Integer.parseInt(query.getPageNumber()),
         Integer.parseInt(query.getPageSize())
     );


     List<DefinicaoRemuneracao> renumeracoes = definicaoRenumeracaoRepository.findAll(filter);

     var content = renumeracoes.stream()
         .map(definicaoRenumeracaoMapper::toDTO)
         .toList();

     long totalElements = content.size();
     int pageNumber = filter.getPageNumber() != null ? filter.getPageNumber() : 0;
     int pageSize = filter.getPageSize() != null ? filter.getPageSize() : 20;
     int totalPages = (int) Math.ceil((double) totalElements / pageSize);


     var wrapper = new WrapperListRenumeracaoDTO();
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
