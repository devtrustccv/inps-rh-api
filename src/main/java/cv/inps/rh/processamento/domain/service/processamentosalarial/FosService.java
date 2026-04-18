package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.DetalhesFosXmlDTO;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.DetalheXmlFosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FosEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.time.YearMonth;

@Transactional
@Service
@RequiredArgsConstructor
public class FosService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FosService.class);

  private final FosEntityRepository fosEntityRepository;
  private final DetalheXmlFosEntityRepository detalheXmlFosEntityRepository;
  private final DataSource dataSource;

  @Transactional(readOnly = true)
  public ListaFosDTO getListaFos(GetListaFosQuery query) {

    var pageable = PageRequest.of(
        Integer.parseInt(query.getPage()),
        Integer.parseInt(query.getSize())
    );

    var startDate = DateFormatter.stringToLocalDateTime(query.getDataInicio());
    var endDate = DateFormatter.stringToLocalDateTime(query.getDataFim());

    var page = fosEntityRepository.findFosProjected(startDate, endDate, pageable);

    var wrapper = new ListaFosDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(page.getContent());

    return wrapper;
  }

  @Transactional(readOnly = true)
  public DetalhesFosXmlDTO getFosDetalhes(Long fosId) {

    var fos = fosEntityRepository.findByIdOrThrow(fosId);

    var rows = detalheXmlFosEntityRepository.findDetalhesByFos(fos.getId());

    return new DetalhesFosXmlDTO(
        fos.getId(),
        fos.getMes(),
        fos.getTpEntrega(),
        fos.getDtEntrega(),
        rows
    );
  }

  public void novosSegurado(Integer ano, Integer mes) {

    var referenceDate = getReferenceDate(ano, mes);

    var currentMonth = LocalDate.now().withDayOfMonth(1);

    if (referenceDate.isAfter(currentMonth))
      throw IgrpResponseStatusException.badRequest("Não pode ser gerado! Mês de Referência não pode ser superior ao mês atual");

    var referenceMonth = buildReferenceMonth(referenceDate);
    LOGGER.debug("Novo segurado reference date: {}", referenceMonth);

    buildSimpleJdbcCall("RH_PK_GERA_XML_DB", "configXML")
        .declareParameters(
            new SqlParameter("p_mes_referencia", Types.VARCHAR),
            new SqlParameter("p_tipo", Types.VARCHAR),
            new SqlParameter("p_user_id", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_mes_referencia", referenceMonth)
                .addValue("p_tipo", "PRIME")
                .addValue("p_user_id", 1) // TODO 18/04/2026 21:34 check this later
        );
  }

  public void restaurar(String referenceMonth, Long fosId) {

    var fosXml = fosEntityRepository.findByIdOrThrow(fosId);
    if (fosXml.getDtEntrega() != null)
      throw IgrpResponseStatusException.badRequest("Já existe uma declaração entregue!");

    LOGGER.debug("Restauration reference date: {}", referenceMonth);

    buildSimpleJdbcCall("RH_PK_GERA_XML_DB", "restaurarXML")
        .declareParameters(
            new SqlParameter("p_mes_referencia", Types.VARCHAR),
            new SqlOutParameter("P_ID", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_mes_referencia", referenceMonth)
                .addValue("P_ID", fosXml.getId())
        );
  }

  public String removerDetalheFos(Long fosDetailId) {

    var obj = detalheXmlFosEntityRepository.findByIdOrThrow(fosDetailId);

    var result = buildSimpleJdbcCall("RH_PK_GERA_XML_DB", "removerBodyXML")
        .declareParameters(
            new SqlParameter("p_id_body_xml", Types.NUMERIC),
            new SqlOutParameter("P_MSG", Types.VARCHAR)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_id_body_xml", obj.getId())
        );

    return (String) result.get("P_MSG");
  }

  private SimpleJdbcCall buildSimpleJdbcCall(String catalogName, String procedureName) {
    return new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName(catalogName)
        .withProcedureName(procedureName);
  }

  private LocalDate getReferenceDate(Integer ano, Integer mes) {

    if (mes == null || mes < 1 || mes > 12)
      throw IgrpResponseStatusException.badRequest("Mês inválido. Deve estar entre 1 e 12");

    return YearMonth.of(ano, mes).atDay(1);
  }

  public String buildReferenceMonth(LocalDate referenceDate) {
    return "%04d%02d".formatted(referenceDate.getYear(), referenceDate.getMonthValue());
  }
}
