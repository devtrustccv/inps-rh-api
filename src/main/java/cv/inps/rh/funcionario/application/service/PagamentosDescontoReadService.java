package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;
import cv.inps.rh.funcionario.application.queries.GetListPagamentosDescontoQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentosDescontoReadService {

  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final DefPagamentoMapper definicaoPagamentoMapper;

  public WrapperListPagamentosDescontoDTO getListPagamentosDesconto(GetListPagamentosDescontoQuery query) {

    int pageNumber = query.getPageNumber() != null  ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    int startRow = (pageNumber - 1) * pageSize + 1;
    int endRow = pageNumber * pageSize;

    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    List<DefPagamentoEntity> pagamentosDescontos = defPagamentoEntityRepository.findAllWithFilter(
        query.getEstado() != null ? query.getEstado() : null,
        dataInicio,
        dataFim,
        startRow,
        endRow
    );

    var content = pagamentosDescontos.stream()
        .map(definicaoPagamentoMapper::toDTO)
        .toList();

    long totalElements = content.size();
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);


    var wrapper = new WrapperListPagamentosDescontoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements(totalElements);
    wrapper.setTotalPages(totalPages);
    wrapper.setFirst(pageNumber == 0);
    wrapper.setLast(pageNumber + 1 >= totalPages);

    return wrapper;
  }
}
