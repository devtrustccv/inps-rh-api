package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.WrapperProcessamentoSalarialDTO;
import cv.inps.rh.processamento.application.queries.GetProcessamentoSalarialQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialReadService {

  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;

  public WrapperProcessamentoSalarialDTO getProcessamentoSalarial(GetProcessamentoSalarialQuery query) {

    var pageRequest = PageRequest.of(Integer.parseInt(query.getPage()), Integer.parseInt(query.getSize()));

    var startDate = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var endDate = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;
    var directionId = StringUtils.hasText(query.getDirecaoId()) ? Long.valueOf(query.getDirecaoId()) : null;
    var type = StringUtils.hasText(query.getTipo()) ? query.getTipo() : null;
    var status = StringUtils.hasText(query.getEstado()) ? query.getEstado() : null;

    var page = processamentoSalarialEntityRepository.list(startDate, endDate, directionId, type, status, pageRequest);

    var response = new WrapperProcessamentoSalarialDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }


}
