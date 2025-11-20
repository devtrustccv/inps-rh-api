package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;


  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    int offset = pageNumber * pageSize;

    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDateTime(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDateTime(query.getDataFim()) : null;

    var funcionarios = funcionarioEntityRepository.findFuncionariosWithFilters(
        query.getNome(),
        query.getDireccao(),
        query.getSeccao(),
        query.getTipoVinculoLaboral(),
        query.getEstado() != null ? query.getEstado() : null,
        dataInicio,
        dataFim,
        offset + 1,
        offset + pageSize
    );


    long totalElements = funcionarios.isEmpty() ? 0 : funcionarios.getFirst().getTotalCount();

    List<FuncionarioListDTO> content = funcionarios.stream()
        .map(funcionarioMapper::toDTO)
        .toList();

    WrapperListaFuncionarioDTO wrapper = new WrapperListaFuncionarioDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements(totalElements);
    wrapper.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
    wrapper.setFirst(pageNumber == 0);
    wrapper.setLast(pageNumber + 1 >= wrapper.getTotalPages());

    return wrapper;
  }
}
