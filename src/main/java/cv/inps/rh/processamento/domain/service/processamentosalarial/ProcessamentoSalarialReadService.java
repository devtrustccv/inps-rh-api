package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.DadosValidacaoDTO;
import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.WrapperProcessamentoSalarialDTO;
import cv.inps.rh.processamento.application.queries.GetDadosValidacaoQuery;
import cv.inps.rh.processamento.application.queries.GetDetalhesProcessamentoQuery;
import cv.inps.rh.processamento.application.queries.GetProcessamentoSalarialQuery;
import cv.inps.rh.processamento.infrastructure.persistence.entity.RhValidacaoEntityView;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.RhValidacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialReadService {

  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final RhValidacaoEntityRepository rhValidacaoRepository;

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

  public ResumoProcessamentoDTO getResumoProcessamentoSalarial(Long procId, Long ccId, Integer ano, Integer mes) {

    var data = new ResumoProcessamentoDTO();
    data.setRemuneracao(procSalCcRemunEntityRepository.getRemuneracoes(procId, ccId, ano, mes));
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

  public List<DadosValidacaoDTO> getDadosValidacao(GetDadosValidacaoQuery f) {

    Specification<RhValidacaoEntityView> spec = (root, query, cb) -> {

      List<Predicate> predicates = new ArrayList<>();

      if (f.getTipoValidacao() != null)
        predicates.add(cb.equal(root.get("tipoValidacao"), f.getTipoValidacao()));

      if (f.getMesAtual() != null)
        predicates.add(cb.equal(root.get("mesAtual"), f.getMesAtual()));

      if (f.getMesAnterior() != null)
        predicates.add(cb.equal(root.get("mesAnterior"), f.getMesAnterior()));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    return rhValidacaoRepository.findAll(spec).stream()
        .map(obj -> new DadosValidacaoDTO(
            obj.getNomeColaborador(),
            obj.getNib(),
            obj.getValorAnterior(),
            obj.getValorAtual(),
            obj.getTipoMovimento(),
            obj.getMesAtual(),
            obj.getValorAtual(),
            obj.getValorEscalao(),
            obj.getNomeColaborador(),
            obj.getNumero(),
            obj.getSituacaoLaboral()
        ))
        .toList();
  }
}
