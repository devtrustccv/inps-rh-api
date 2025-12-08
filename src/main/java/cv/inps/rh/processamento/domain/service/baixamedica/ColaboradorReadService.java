package cv.inps.rh.processamento.domain.service.baixamedica;

import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
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

  public WrapperListaColaboradorDTO getListaBaixaMedica(GetListaBaixamedicaQuery query) {
    return getListas(query.getPage(), query.getSize(), query.getDataInicio(), query.getDataFim(), query.getDireccao(), query.getColaborador());
  }

  public WrapperListaColaboradorDTO getListaLicensaSemvencimento(GetListaLicensaSemVencimentoQuery query) {
    return getListas(query.getPage(), query.getSize(), query.getDataInicio(), query.getDataFim(), query.getDireccao(), query.getColaborador());
  }

  private WrapperListaColaboradorDTO getListas(String page, String size, String dataInicio, String dataFim, String direccao, String colaborador) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(page),
        Integer.parseInt(size)
    );

    var startDate = StringUtils.hasText(dataInicio) ? DateFormatter.stringToLocalDate(dataInicio) : null;
    var endDate = StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDate(dataFim) : null;
    var directionId = StringUtils.hasText(direccao) ? Long.valueOf(direccao) : null;
    var funcionario = StringUtils.hasText(colaborador) ? colaborador : null;

    var pageData = tiposRelacionamentoEntityRepository.getListaColaboradores(directionId, funcionario, startDate, endDate, pageRequest);
    pageData.forEach(obj -> obj.setEstadoSituacaoLaboralDesc(obj.getEstadoSituacaoLaboral().getDescription()));

    // TODO 06/12/2025 18:19 falta defenir condicao pa saber k o registo é uma LICENSA SEM VENCIMENTO / BAIXA MEDICA ???
    // TODO 06/12/2025 18:19 ESTADO P, A ???

    var response = new WrapperListaColaboradorDTO();
    PageMapper.fillPagination(pageData, response);
    response.setContent(pageData.getContent());
    return response;
  }

}
