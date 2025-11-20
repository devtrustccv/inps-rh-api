package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.queries.GetListRegimesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegimeReadService {

  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;

  public WrapperRegimeListDTO listRegime(GetListRegimesQuery query) {

    var pageNumber = query.getPageNumber()!=null ?  Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = query.getPageSize()!=null ? Integer.parseInt(query.getPageSize()) : 20;

    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    List<RegimeTrabalhoEntity> regimes = regimeTrabalhoEntityRepository.findAllWithFilter(
        query.getTipoRegime(),
        query.getEstado() != null ? query.getEstado(): null,
        startRow,
        endRow
    );

    var content = regimes.stream()
        .map(regimeTrabalhoMapper::toDTO)
        .toList();

    long totalElements = content.size();
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);


    var wrapper = new WrapperRegimeListDTO();
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
