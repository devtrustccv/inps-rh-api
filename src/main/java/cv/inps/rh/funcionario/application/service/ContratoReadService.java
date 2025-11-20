package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.queries.GetListContratosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoReadService {

  private final ContratoMapper contratoMapper;
  private final ContratoEntityRepository contratoEntityRepository;

  public WrapperListContratoDTO listaContratos(GetListContratosQuery query) {

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).toString();

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 10;

    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    var contratos = contratoEntityRepository.findAllWithPagination(
        query.getVinculo(),
        idFuncionario,
        startRow,
        endRow
    );

    var content = contratos.stream()
        .map(contratoMapper::toDTO)
        .toList();

    long totalElements = content.size();
    var totalPages = (int) Math.ceil((double) totalElements / pageSize);

    // Montar wrapper DTO
    var wrapper = new WrapperListContratoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements(totalElements);
    wrapper.setTotalPages(totalPages);
    wrapper.setFirst(true);
    wrapper.setLast(pageNumber + 1 >= totalPages);

    return wrapper;

  }
}
