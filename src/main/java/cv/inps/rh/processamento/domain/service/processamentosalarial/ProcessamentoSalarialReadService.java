package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.WrapperProcessamentoSalarialDTO;
import cv.inps.rh.processamento.application.queries.GetDetalhesProcessamentoQuery;
import cv.inps.rh.processamento.application.queries.GetProcessamentoSalarialQuery;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
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
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;

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

  public ResumoProcessamentoDTO getResumoProcessamentoSalarial(String processamentoId) {

    var procId = Long.valueOf(processamentoId);

    var data = new ResumoProcessamentoDTO();
    data.setRemuneracao(procSalCcRemunEntityRepository.getRemuneracoes(procId));
    data.setPagamento(procSalCcPagEntityRepository.getPagamentos(procId));

    return data;
  }

  public DetalhesProcessamentoDTO getDetalhesProcessamentoSalarial(GetDetalhesProcessamentoQuery query) {

    var isPayment = TipoDetalhe.valueOf(query.getTipoDetalhe()).equals(TipoDetalhe.PAGAMENTO);

    var procId = Long.valueOf(query.getProcSalId());

    var data = new DetalhesProcessamentoDTO();
    data.setContent(isPayment ?
        procSalCcPagEntityRepository.getDetalhesPagamento(query.getTipoMovimento(), procId) :
        procSalCcRemunEntityRepository.getDetalhesRemuneracao(query.getTipoMovimento(), procId));

    return data;
  }

  private enum TipoDetalhe {
    REMUNERACAO, PAGAMENTO
  }
}
