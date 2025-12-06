package cv.inps.rh.processamento.domain.service.baixamedica;

import cv.inps.rh.processamento.application.dto.WrapperListaBaixaMedicaDTO;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BaixaMedicaReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public WrapperListaBaixaMedicaDTO getListaBaixaMedica(GetListaBaixamedicaQuery query) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPage()),
        Integer.parseInt(query.getSize())
    );

    var startDate = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var endDate = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;
    var directionId = StringUtils.hasText(query.getDireccao()) ? Long.valueOf(query.getDireccao()) : null;
    var funcionario = StringUtils.hasText(query.getColaborador()) ? query.getColaborador() : null;

    var page = tiposRelacionamentoEntityRepository.getBaixaMedica(directionId, funcionario, startDate, endDate, pageRequest);
    page.forEach(obj -> obj.setEstadoSituacaoLaboralDesc(obj.getEstadoSituacaoLaboral().getDescription()));

    // TODO 06/12/2025 18:19 falta defenir condicao pa saber k o registo é uma BAIXA MEDICA ???
    // TODO 06/12/2025 18:19 ESTADO P, A ???

    var response = new WrapperListaBaixaMedicaDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

}
