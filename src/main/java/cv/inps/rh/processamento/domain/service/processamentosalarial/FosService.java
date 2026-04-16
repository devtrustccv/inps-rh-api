package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FosEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;

@Transactional
@Service
@RequiredArgsConstructor
public class FosService {

  private final FosEntityRepository fosEntityRepository;
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

  public void novosFos(Integer ano, Integer mes) {

    if (mes == null || mes < 1 || mes > 12)
      throw IgrpResponseStatusException.badRequest("Mês inválido. Deve estar entre 1 e 12");

    var referencia = LocalDate.of(ano, mes, 1);

    var currentMonth = LocalDate.now().withDayOfMonth(1);

    if (referencia.isAfter(currentMonth))
      throw IgrpResponseStatusException.badRequest("Não pode ser gerado! Mês de Referência não pode ser superior ao mês atual");

    var mesReferencia = String.format("%04d%02d", ano, mes);

    new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_GERA_XML_DB")
        .withProcedureName("configXML")
        .declareParameters(
            new SqlParameter("p_mes_referencia", Types.VARCHAR),
            new SqlParameter("p_tipo", Types.VARCHAR)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_mes_referencia", mesReferencia)
                .addValue("p_tipo", "PRIME")
        );
  }
}
