package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.queries.GetValicoesUtilizadoresQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ValidacaoMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidacoesReadService {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ValidacaoMapper validacaoMapper;

  public WrapperListaValidacoesDTO listaValidacoes(GetValicoesUtilizadoresQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    int startRow = pageNumber * pageSize + 1;
    int endRow = startRow + pageSize - 1;

    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDateTime(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDateTime(query.getDataFim()) : null;

    var validacoes = validacaoEntityRepository.findAllWithFilters(
        query.getNomeColaborador(),
        query.getTipoOperacao(),
        query.getReferenciaName(),
        dataInicio,
        dataFim,
        startRow,
        endRow
    );

    var content = validacoes.stream()
        .map(validacaoMapper::toDto)
        .toList();

    // Paginação
    long totalElements = content.size();
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

    return wrapper;
  }
}
