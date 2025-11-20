package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.queries.GetListRenumeracoesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RenumeracoesReadService {

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;

  public WrapperListRenumeracaoDTO getListRenumeracoes(GetListRenumeracoesQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    int startRow = (pageNumber - 1) * pageSize + 1;
    int endRow = pageNumber * pageSize;

    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    List<DefinicaoRemuneracaoEntity> renumeracoes = definicaoRemuneracaoEntityRepository.findAllWithFilter(
        query.getEstado() != null ? query.getEstado() : null,
        dataInicio,
        dataFim,
        startRow,
        endRow
    );

    var content = renumeracoes.stream()
        .map(definicaoRemuneracaoMapper::toDTO)
        .toList();

    long totalElements = content.size();
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);


    var wrapper = new WrapperListRenumeracaoDTO();
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
