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
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVListaProcessamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.NumberUtils;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialReadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialReadService.class);

  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final RhVListaProcessamentoEntityRepository listaProcessamentoEntityRepository;
  private final ObjectMapper objectMapper;
  private final DataSource dataSource;

  public WrapperProcessamentoSalarialDTO getProcessamentoSalarial(GetProcessamentoSalarialQuery query) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPage()),
        Integer.parseInt(query.getSize())
    );

    var startDate = dataDoFiltro(query.getDataInicio(), "dataInicio");
    var endDate = dataDoFiltro(query.getDataFim(), "dataFim");
    if (startDate != null && endDate != null && endDate.isBefore(startDate))
      throw IgrpResponseStatusException.badRequest("Data Fim não pode ser anterior à Data Início.");
    var directionId = StringUtils.hasText(query.getDirecaoId()) ? Long.valueOf(query.getDirecaoId()) : null;
    var type = StringUtils.hasText(query.getTipo()) ? query.getTipo() : null;
    var status = StringUtils.hasText(query.getEstado()) ? query.getEstado() : null;

    var page = listaProcessamentoEntityRepository.list(
        startDate,
        endDate,
        directionId,
        type,
        status,
        pageRequest
    );

    var response = new WrapperProcessamentoSalarialDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  /**
   * Data de um filtro da lista. Aceita só data ou data com hora (ex.: "2026-10-01T00:00:00.000Z", que é o
   * que o ecrã envia) e fica com a parte da data. Antes uma data com hora passava a null sem aviso e a lista
   * vinha sem filtro (QA PS-006); agora uma data que não se percebe dá 400.
   */
  private static LocalDate dataDoFiltro(String valor, String campo) {
    if (!StringUtils.hasText(valor)) return null;
    var data = DateFormatter.stringToLocalDate(valor.trim());
    if (data == null && valor.trim().length() > 10)
      data = DateFormatter.stringToLocalDate(valor.trim().substring(0, 10));
    if (data == null)
      throw IgrpResponseStatusException.badRequest("Data inválida em '" + campo + "': " + valor);
    return data;
  }

  private static String formatar(DecimalFormat format, Long valor) {
    return valor == null ? null : format.format(valor);
  }

  public ResumoProcessamentoDTO getResumoProcessamentoSalarial(List<Long> procIds) {

    var processedIds = procIds.stream().filter(Objects::nonNull).distinct().toList();

    var isOne = processedIds.size() == 1;

    var remuneracoes = isOne ? procSalCcRemunEntityRepository.getRemuneracoesForOne(processedIds.getFirst()) : procSalCcRemunEntityRepository.getRemuneracoes(processedIds);
    var pagamentos = isOne ? procSalCcPagEntityRepository.getPagamentosForOne(processedIds.getFirst()) : procSalCcPagEntityRepository.getPagamentos(processedIds);

    var format = NumberUtils.spaceDecimalFormat();

    remuneracoes.forEach(r -> {
      r.setQuantidadeFormatada(formatar(format, r.getQuantidade()));
      r.setTotalFormatado(formatar(format, r.getTotal()));
    });

    pagamentos.forEach(p -> {
      p.setQuantidadeFormatada(formatar(format, p.getQuantidade()));
      p.setValorFormatado(formatar(format, p.getValor()));
    });

    return new ResumoProcessamentoDTO(remuneracoes, pagamentos);
  }

  public DetalhesProcessamentoDTO getDetalhesProcessamentoSalarial(GetDetalhesProcessamentoQuery query) {

    var isPayment = TipoDetalhe.valueOf(query.getTipoDetalhe()).equals(TipoDetalhe.PAGAMENTO);

    var procId = Long.valueOf(query.getProcSalId());

    var detalhes = isPayment ?
        procSalCcPagEntityRepository.getDetalhesPagamento(query.getTipoMovimento(), procId, query.getIdItemAto()) :
        procSalCcRemunEntityRepository.getDetalhesRemuneracao(query.getTipoMovimento(), procId, query.getIdItemAto());

    var format = NumberUtils.spaceDecimalFormat();
    detalhes.forEach(d -> d.setValorFormatado(formatar(format, d.getValor())));

    return new DetalhesProcessamentoDTO(detalhes);
  }

  public List<DadosValidacaoDTO> getDadosValidacao(GetDadosValidacaoQuery query) {
    try {

      var sql = "{ ? = call RH_PK_VALIDACAO_SALARIAL_DB.RH_F_VALIDACAO2(?, ?) }";

      try (var connection = dataSource.getConnection();
           var stmt = connection.prepareCall(sql).unwrap(OracleCallableStatement.class)) {

        stmt.registerOutParameter(1, OracleTypes.CLOB);

        var ids = query.getProcessamentoIds().toArray(String[]::new);

        stmt.setPlsqlIndexTable(
            2,
            ids,
            ids.length,
            ids.length,
            OracleTypes.VARCHAR,
            32767
        );

        stmt.setString(3, query.getTipoValidacao());
        stmt.execute();

        var clob = stmt.getClob(1);
        if (clob == null)
          return List.of();

        var json = clob.getSubString(1, (int) clob.length());
        if (!StringUtils.hasText(json))
          return List.of();

        return objectMapper.readValue(
            json,
            new TypeReference<>() {
            }
        );
      }

    } catch (Exception e) {
      LOGGER.error("Error getting validation data", e);
      throw IgrpResponseStatusException.internalServerError(
          "Error getting data: " + e.getMessage()
      );
    }
  }

  private enum TipoDetalhe {
    REMUNERACAO, PAGAMENTO
  }
}
