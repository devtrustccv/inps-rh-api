package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.queries.GetCarreiraListQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreiraReadService {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final CarreiraMapper carreiraMapper;

  public WrapperCarreiraListDTO list(GetCarreiraListQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).toString();
    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    long startRow = (long) pageNumber * pageSize + 1;
    long endRow = startRow + pageSize - 1;

    var list = carreiraEntityRepository.findAllCarreiras(
        query.getTipoCarreira(),
        dataInicio,
        dataFim,
        startRow,
        endRow,
        idFuncionario
    );

    long total = list.isEmpty() ? 0 : list.getFirst().getTotalCount();

    List<CarreiraListDTO> content = list.stream()
        .map(carreiraMapper::toDTO)
        .toList();

    var wrapper = new WrapperCarreiraListDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements(total);
    wrapper.setTotalPages((int) Math.ceil((double) total / pageSize));
    wrapper.setFirst(pageNumber == 0);
    wrapper.setLast(pageNumber + 1 >= wrapper.getTotalPages());

    return wrapper;


  }
}
