package cv.inps.rh.processamento.domain.service.processamentosalarial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.processamento.application.dto.DadosValidacaoDTO;
import cv.inps.rh.processamento.application.dto.DetalhesProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.ResumoProcessamentoDTO;
import cv.inps.rh.processamento.application.dto.WrapperProcessamentoSalarialDTO;
import cv.inps.rh.processamento.application.queries.GetDadosValidacaoQuery;
import cv.inps.rh.processamento.application.queries.GetDetalhesProcessamentoQuery;
import cv.inps.rh.processamento.application.queries.GetProcessamentoSalarialQuery;
import cv.inps.rh.processamento.domain.service.processamentosalarial.validacao.DadosValidacao;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Clob;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialReadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialReadService.class);

  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final ObjectMapper objectMapper;

  @PersistenceContext
  private EntityManager entityManager;


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

  public List<DadosValidacaoDTO> getDadosValidacao(GetDadosValidacaoQuery query) {
    try {

      if (!StringUtils.hasText(query.getTipoValidacao())
          && !StringUtils.hasText(query.getMesAtual())
          && !StringUtils.hasText(query.getMesAnterior()))
        return List.of();

      var clob = (Clob) entityManager
          .createNativeQuery("SELECT RH_F_VALIDACAO(:tipo, :mesAnterior, :mesAtual, :idsProcessamento) FROM dual")
          .setParameter("tipo", query.getTipoValidacao())
          .setParameter("mesAnterior", query.getMesAnterior())
          .setParameter("mesAtual", query.getMesAtual())
          .setParameter("idsProcessamento", query.getProcessamentoIds())
          .getSingleResult();

      if (clob == null)
        return List.of();

      var json = clob.getSubString(1, (int) clob.length());

      return objectMapper.readValue(json, new TypeReference<List<DadosValidacao>>() {
          })
          .stream()
          .map(obj -> {
            var row = new DadosValidacaoDTO();
            row.setNomeColaborador(obj.getNomeColaborador());
            row.setNib(obj.getNib());
            row.setValorAnterior(obj.getValorAnterior());
            row.setValorAtual(obj.getValorAtual());
            row.setTipoMovimento(obj.getTipoMovimento());
            row.setMesAnterior(obj.getMesAnterior());
            row.setMesAtual(obj.getMesAtual());
            row.setValorEscalao(obj.getValorEscalao());
            row.setNumero(obj.getNumero());
            row.setSituacaoLaboral(obj.getSituacaoLaboral());
            row.setTipoFiltro(obj.getTipoFiltro());
            row.setProcessamentoId(obj.getProcsalId());
            row.setFunId(obj.getFunId());
            return row;
          })
          .toList();

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    throw IgrpResponseStatusException.internalServerError("Error getting data...");
  }

  private enum TipoDetalhe {
    REMUNERACAO, PAGAMENTO
  }
}
