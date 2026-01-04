package cv.inps.rh.processamento.domain.service.pesquisa;

import cv.inps.rh.processamento.application.dto.WrapperPesquisaCentroCustoDTO;
import cv.inps.rh.processamento.application.dto.WrapperPesquisaColaboradorDTO;
import cv.inps.rh.processamento.application.queries.PesquisaCentroCustoQuery;
import cv.inps.rh.processamento.application.queries.PesquisaColaboradorQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.PageRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PesquisaService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public WrapperPesquisaColaboradorDTO pesquisaColaborador(PesquisaColaboradorQuery query) {

    var pageRequest = PageRequestUtil.buildPageRequest(query.getPage(), query.getSize());

    var directionId = StringUtils.hasText(query.getDireccao()) ? Long.valueOf(query.getDireccao()) : null;
    var nome = StringUtils.hasText(query.getNome()) ? query.getNome() : null;
    // TODO 06/12/2025 18:46 validate this Centro custo
    //var centroCusto = StringUtils.hasText(query.getCentroCusto()) ? query.getCentroCusto() : null;

    var page = tiposRelacionamentoEntityRepository.pesquisaColaborador(directionId, nome, pageRequest);

    var response = new WrapperPesquisaColaboradorDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  public WrapperPesquisaCentroCustoDTO pesquisaCentroCusto(PesquisaCentroCustoQuery query) {

    var pageRequest = PageRequestUtil.buildPageRequest(query.getPage(), query.getSize());

    var nome = StringUtils.hasText(query.getNome()) ? query.getNome() : null;

    var page = tiposRelacionamentoEntityRepository.pesquisaCentroCusto(nome, pageRequest);

    var response = new WrapperPesquisaCentroCustoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

}
