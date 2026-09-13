package cv.inps.rh.processamento.domain.service.baixamedica;

import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.application.queries.GetListaLicensaSemVencimentoQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ColaboradorReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public WrapperListaColaboradorDTO getListaLicensaSemvencimento(GetListaLicensaSemVencimentoQuery query) {

    var pageData = tiposRelacionamentoEntityRepository.getListaColaboradores(
        StringUtils.hasText(query.getDireccao()) ? Long.valueOf(query.getDireccao()) : null,
        StringUtils.hasText(query.getColaborador()) ? query.getColaborador() : null,
        StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null,
        StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null,
        PageRequest.of(
            Integer.parseInt(query.getPage()),
            Integer.parseInt(query.getSize())
        )
    );
    pageData.forEach(obj -> obj.setEstadoSituacaoLaboralDesc(obj.getEstadoSituacaoLaboral().getDescription()));

    var response = new WrapperListaColaboradorDTO();
    PageMapper.fillPagination(pageData, response);
    response.setContent(pageData.getContent());
    return response;
  }
}
