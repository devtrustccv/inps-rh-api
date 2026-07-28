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
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVListaProcessamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialReadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialReadService.class);

  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final RhVListaProcessamentoEntityRepository listaProcessamentoEntityRepository;
  private final ObjectMapper objectMapper;
  private final DataSource dataSource;

  @PersistenceContext
  private EntityManager entityManager;


  public WrapperProcessamentoSalarialDTO getProcessamentoSalarial(GetProcessamentoSalarialQuery query) {

    var pageRequest = PageRequest.of(Integer.parseInt(query.getPage()), Integer.parseInt(query.getSize()));

    var startDate = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var endDate = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;
    var directionId = StringUtils.hasText(query.getDirecaoId()) ? Long.valueOf(query.getDirecaoId()) : null;
    var type = StringUtils.hasText(query.getTipo()) ? query.getTipo() : null;
    var status = StringUtils.hasText(query.getEstado()) ? query.getEstado() : null;

    var page = listaProcessamentoEntityRepository.list(startDate, endDate, directionId, type, status, pageRequest);

    var response = new WrapperProcessamentoSalarialDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  public ResumoProcessamentoDTO getResumoProcessamentoSalarial(List<Long> procIds) {

    var processedIds = procIds.stream().filter(Objects::nonNull).distinct().toList();

    var isOne = processedIds.size() == 1;

    return new ResumoProcessamentoDTO(
        isOne ? procSalCcRemunEntityRepository.getRemuneracoesForOne(processedIds.getFirst()) : procSalCcRemunEntityRepository.getRemuneracoes(processedIds),
        isOne ? procSalCcPagEntityRepository.getPagamentosForOne(processedIds.getFirst()) : procSalCcPagEntityRepository.getPagamentos(processedIds)
    );
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

      var sql = "{ ? = call RH_PK_VALIDACAO_SALARIAL_DB.RH_F_VALIDACAO2(?, ?) }";

      try (var connection = dataSource.getConnection();
           var stmt = connection.prepareCall(sql).unwrap(OracleCallableStatement.class);) {

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
