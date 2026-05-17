package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.AumentoListDTO;
import cv.inps.rh.processamento.application.queries.GetListaAumentoSalarialQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AumentoSalarialReadService {

  private final AumentoSalarialEntityRepository aumentoSalarialEntityRepository;

  public AumentoListDTO getProcessamentoSalarial(GetListaAumentoSalarialQuery query) {

    var pageRequest = PageRequest.of(query.getPage(), query.getSize());

    var year = Objects.nonNull(query.getAno()) ? Year.of(query.getAno()) : null;
    var startDate = Objects.nonNull(year) ? year.atDay(1) : null;
    var endDate = Objects.nonNull(year) ? year.atMonth(Month.DECEMBER).atEndOfMonth() : null;

    var page = aumentoSalarialEntityRepository.list(startDate, endDate, pageRequest);

    var response = new AumentoListDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }
}
