package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.commands.AdicionarFuncionarioCommand;
import cv.inps.rh.processamento.application.dto.DetalheXmlRequestDTO;
import cv.inps.rh.processamento.application.dto.DetalhesFosXmlDTO;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
import cv.inps.rh.processamento.domain.service.processamentosalarial.util.FosUtil;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.DetalheXmlFosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FosEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.time.LocalDate;

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
    var page = fosEntityRepository.getFos(startDate, endDate, pageable);

    var wrapper = new ListaFosDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(page.getContent());

    return wrapper;
  }

  @Transactional(readOnly = true)
  public DetalhesFosXmlDTO getFosDetalhes(Long fosId, Integer directionId) {

    var fos = fosEntityRepository.findByIdOrThrow(fosId);

    var fosDetails = detalheXmlFosEntityRepository.findDetalhesByFos(fos.getId(), directionId);

    return new DetalhesFosXmlDTO(
        fos.getId(),
        fos.getMes(),
        fos.getTpEntrega(),
        fos.getDtEntrega(),
        fosDetails
    );
  }

  public void novoSegurado(Integer ano, Integer mes) {

    var referenceDate = FosUtil.getReferenceDate(ano, mes);

    var currentMonth = LocalDate.now().withDayOfMonth(1);

    if (referenceDate.isAfter(currentMonth))
      throw IgrpResponseStatusException.badRequest("Mês de Referência não pode ser superior ao mês atual");

    var referenceMonth = FosUtil.buildReferenceMonth(referenceDate);
    LOGGER.debug("Novo segurado reference date: {}", referenceMonth);

    configXml(referenceMonth, "PRIME");
  }

  private void configXml(String referenceMonth, String tipo) {
    buildSimpleJdbcCall("configXML")
        .declareParameters(
            new SqlParameter("p_mes_referencia", Types.VARCHAR),
            new SqlParameter("p_tipo", Types.VARCHAR),
            new SqlParameter("p_user_id", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_mes_referencia", referenceMonth)
                .addValue("p_tipo", tipo)
                .addValue("p_user_id", 1) // TODO 18/04/2026 21:34 check this later
        );
  }

  public void restaurarXml(Long fosId) {

    var fosXml = fosEntityRepository.findByIdOrThrow(fosId);
    FosUtil.validateDeliveryDate(fosXml.getDtEntrega());

    final var referenceMonth = fosXml.getMes() + fosXml.getAno();
    final var type = fosXml.getTpEntrega();

    detalheXmlFosEntityRepository.deleteAllByIdXmlFos(fosXml);
    fosEntityRepository.delete(fosXml);

    configXml(referenceMonth, type);
  }

  public void removerFos(Long fosId) {

    var fosXml = fosEntityRepository.findByIdOrThrow(fosId);

    FosUtil.validateDeliveryDate(fosXml.getDtEntrega());

    detalheXmlFosEntityRepository.deleteAllByIdXmlFos(fosXml);

    fosEntityRepository.delete(fosXml);
  }

  public void removerDetalheFos(Long fosDetailId) {

    var detail = detalheXmlFosEntityRepository.findByIdOrThrow(fosDetailId);

    FosUtil.validateDeliveryDate(detail.getIdXmlFos().getDtEntrega());

    var xmlFos = detail.getIdXmlFos();

    detalheXmlFosEntityRepository.delete(detail);

    var sum = BigDecimal.valueOf(detalheXmlFosEntityRepository.sumVlRemunManByXmlId(xmlFos.getId()));

    var totalContribution = sum
        .multiply(BigDecimal.valueOf(0.245))
        .setScale(2, RoundingMode.HALF_UP);

    xmlFos.setTtRemuneracao(sum.toString());
    xmlFos.setTtContribuicao(totalContribution.toString());
    xmlFos.setTtContribCalc(totalContribution.longValue());
    fosEntityRepository.save(xmlFos);
  }

  public void gravarNovaLinhaFos(DetalheXmlRequestDTO dto) {

    buildSimpleJdbcCall("gravarNovaLinhaXML")
        .declareParameters(
            new SqlParameter("p_id_xml", Types.NUMERIC),
            new SqlParameter("p_id_row", Types.VARCHAR),
            new SqlParameter("p_mes_ref", Types.VARCHAR),
            new SqlParameter("p_tp_remuneracao", Types.VARCHAR),
            new SqlParameter("p_remuneracao", Types.NUMERIC),
            new SqlParameter("p_dia_trab", Types.NUMERIC),
            new SqlParameter("p_fun_id", Types.NUMERIC),
            new SqlParameter("p_nu_segurado", Types.VARCHAR),
            new SqlParameter("p_dir_serv_id", Types.NUMERIC),
            new SqlParameter("p_user_id", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_id_xml", dto.getFosId())
                .addValue("p_id_row", FosUtil.normalizeIdRow(dto.getDetaildId()))
                .addValue("p_mes_ref", dto.getMesReferencia())
                .addValue("p_tp_remuneracao", dto.getTipoRemuneracao())
                .addValue("p_remuneracao", dto.getRemuneracao())
                .addValue("p_dia_trab", dto.getDiasTrabalho())
                .addValue("p_fun_id", dto.getFuncionarioId())
                .addValue("p_nu_segurado", dto.getNumeroSegurado())
                .addValue("p_dir_serv_id", dto.getDirecaoServicoId())
                .addValue("p_user_id", 1) // TODO 18/04/2026 22:53 see this
        );
  }

  public void adicionarFuncionario(AdicionarFuncionarioCommand command) {

    var fosXml = fosEntityRepository.findByIdOrThrow(command.getFosId());
    FosUtil.validateDeliveryDate(fosXml.getDtEntrega());

    var referenceDate = FosUtil.getReferenceDate(command.getAno(), command.getMes());
    var currentMonth = LocalDate.now().withDayOfMonth(1);
    if (referenceDate.isAfter(currentMonth))
      throw IgrpResponseStatusException.badRequest("Não pode ser gerado! Mês de Referência não pode ser superior ao mês atual");

    var referenceMonth = FosUtil.buildReferenceMonth(referenceDate);
    LOGGER.debug("Novo funcionario reference date: {}", referenceMonth);

    buildSimpleJdbcCall("configSeguradoXML")
        .declareParameters(
            new SqlParameter("p_mes_referencia", Types.VARCHAR),
            new SqlParameter("p_id_xml_fos", Types.NUMERIC),
            new SqlParameter("p_nr_segurado", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_mes_referencia", referenceMonth)
                .addValue("p_id_xml_fos", command.getFosId())
                .addValue("p_nr_segurado", command.getNumeroSegurado())
        );
  }

  public void enviarFolha(Long fosId) {

    var fosXml = fosEntityRepository.findByIdOrThrow(fosId);

    FosUtil.validateDeliveryDate(fosXml.getDtEntrega());

    // TODO 18/04/2026 23:21 api INPS
  }

  public void substituirXml(Long id) {

    var fosXml = fosEntityRepository.findByIdOrThrow(id);

    FosUtil.validateDeliveryDate(fosXml.getDtEntrega());

    configXml(fosXml.getAno() + fosXml.getMes(), "SUBST");
  }

  private SimpleJdbcCall buildSimpleJdbcCall(String procedureName) {
    return new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_GERA_XML_DB")
        .withProcedureName(procedureName);
  }
}
