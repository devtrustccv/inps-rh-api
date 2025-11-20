package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.service.FuncionarioReadService;
import cv.inps.rh.funcionario.domain.filters.FuncionarioFilter;
import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetListFuncionariosQueryHandler implements QueryHandler<GetListFuncionariosQuery, ResponseEntity<WrapperListaFuncionarioDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListFuncionariosQueryHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final FuncionarioMapper funcionarioMapper;

  private final FuncionarioReadService funcionarioReadService;

  public GetListFuncionariosQueryHandler(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper, FuncionarioReadService funcionarioReadService) {

    this.funcionarioRepository = funcionarioRepository;
    this.funcionarioMapper = funcionarioMapper;
    this.funcionarioReadService = funcionarioReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListaFuncionarioDTO> handle(GetListFuncionariosQuery query) {
    LOGGER.info("Handling GetListFuncionariosQuery: {}", query);

    return ResponseEntity.ok(funcionarioReadService.getListFuncionarios(query));

    /*FuncionarioFilter filters = funcionarioMapper.toFilterDomain(
        query.getNome(),
        query.getDireccao(),
        query.getSeccao(),
        query.getTipoVinculoLaboral(),
        query.getDataInicio(),
        query.getDataFim(),
        query.getEstado(),
        Integer.parseInt(query.getPageNumber()),
        Integer.parseInt(query.getPageSize())
    );

    List<FuncionarioList> funcionarios = funcionarioRepository.findAll(filters);

    long totalElements = funcionarios.isEmpty() ? 0 : funcionarios.getFirst().getTotalCount();

    List<FuncionarioListDTO> content = funcionarios.stream()
        .map(funcionarioMapper::toDTO)
        .toList();

    WrapperListaFuncionarioDTO wrapper = new WrapperListaFuncionarioDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(filters.getPageNumber());
    wrapper.setPageSize(filters.getPageSize());
    wrapper.setTotalElements(totalElements);
    wrapper.setTotalPages((int) Math.ceil((double) totalElements / filters.getPageSize()));
    wrapper.setFirst(filters.getPageNumber() == 0);
    wrapper.setLast(filters.getPageNumber() + 1 >= wrapper.getTotalPages());

    return ResponseEntity.ok(wrapper);*/

  }
}
