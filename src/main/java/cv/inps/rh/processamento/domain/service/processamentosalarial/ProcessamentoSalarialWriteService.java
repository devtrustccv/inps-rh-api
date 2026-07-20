package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.processamento.application.constants.TipoValidacaoProcessamentoSalarial;
import cv.inps.rh.processamento.application.dto.ProcessamentoSalarioRequestDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.api.ProcessarSalarioApi;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Transactional
@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialWriteService.class);

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final ProcessarSalarioApi processarSalarioApi;
  private final DataSource dataSource;
  private final JdbcTemplate jdbcTemplate;

  public void removerFuncionariosProcessados(List<String> funcionariosIds) {

    var ids = funcionariosIds.stream().map(UUID::fromString).toList();

    var relations = tiposRelacionamentoEntityRepository.findRelacionamentosAtuaisByFuncionarioUuids(ids);
    if (relations.isEmpty())
      return;

    relations.forEach(r -> r.setFlgProcessa(0));
    tiposRelacionamentoEntityRepository.saveAll(relations);
  }

  public String eliminarProcessamento(List<Long> ids) {

    var invalidStatus = List.of(
        StatusProcessamento.PROCESSADO.name(),
        StatusProcessamento.ERRO_PROCESSAMENTO.name()
    );

    var invalidValuesToBeEliminated = processamentoSalarialEntityRepository.findAllById(ids)
        .stream()
        .map(ProcessamentoSalarialEntity::getEstado)
        .filter(Objects::nonNull)
        .filter(obj -> !invalidStatus.contains(obj))
        .toList();
    if (!invalidValuesToBeEliminated.isEmpty())
      return "Existem registos que nao podem ser eliminados nestes estados: %s".formatted(invalidValuesToBeEliminated.toString());

    var processingIds = ids.stream()
        .map(String::valueOf)
        .toArray(String[]::new);

    return jdbcTemplate.execute((Connection con) -> {

      var oracleCon = con.unwrap(OracleConnection.class);

      var stmt = (OracleCallableStatement) oracleCon.prepareCall("{ call RH_PROCESSAMENTO_SALARIAL_DB.ELIMINAR_PROCESSAMENTO(?, ?) }");

      stmt.setPlsqlIndexTable(
          1,
          ids,
          processingIds.length,
          processingIds.length,
          OracleTypes.VARCHAR,
          4000
      );

      stmt.registerOutParameter(2, Types.VARCHAR);

      stmt.execute();

      return stmt.getString(2);
    });
  }

  public void validar(List<Long> ids, TipoValidacaoProcessamentoSalarial tipoValidacao) {

    if (Objects.isNull(tipoValidacao))
      throw IgrpResponseStatusException.badRequest("Para validar deve indicar o tipo de validacao: [DEFINITIVO, PROVISORIO]");

    var processesThatCanNotBeValidated = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(ids);

    var status = TipoValidacaoProcessamentoSalarial.DEFINITIVO.equals(tipoValidacao) ? StatusProcessamento.VALIDADO_PROVISORIO : StatusProcessamento.PROCESSADO;

    processes.forEach(process -> {
      if (!status.name().equals(process.getEstado()))
        processesThatCanNotBeValidated.add(process.getId());
      else
        process.setEstado(StatusProcessamento.VALIDADO.name());
    });

    if (!processesThatCanNotBeValidated.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado %s".formatted(status.name()));

    processamentoSalarialEntityRepository.saveAll(processes);
  }

  public void cabimentar(List<Long> ids) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    processes.forEach(process -> {
      if (!process.getEstado().equals(StatusProcessamento.VALIDADO_DEFINITIVO.name()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'VALIDADO_DEFINITIVO'", illegalProcesses);

    processes.forEach(p -> {
      var date = p.getDataProcDefinitivo().format(DateFormatter.DATE);
      var response = processarSalarioApi.processarCabimento(p.getId().toString(), date);
      LOGGER.debug("Cabimentar Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao processar cabimento", response.content().issue().diagnostics());
    });
  }

  public void autorizar(List<Long> ids) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    processes.forEach(process -> {
      if (!process.getEstado().equals(StatusProcessamento.CABIMENTADO.name()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'CABIMENTADO'", illegalProcesses);

    processes.forEach(p -> {
      var response = processarSalarioApi.autorizarSalario(p.getCab1Id().toString(), "SIM");
      LOGGER.debug("Autorizar Salario Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao autorizar salario", response.content().issue().diagnostics());
    });
  }

  public void extornarCabimento(List<Long> ids) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.ELIMINAR_CABIMENTO.getCode()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'DEV'", illegalProcesses);

    processes.forEach(p -> {
      var response = processarSalarioApi.extornarCabimento(p.getCab1Id().toString());
      LOGGER.info("Extornar Cabimento Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao extornar cabimento", response.content().issue().diagnostics());
    });
  }

  public String processarSalario(ProcessamentoSalarioRequestDTO request) {

    var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    var startDate = Objects.nonNull(request.getDataInicio()) ? request.getDataInicio().format(formatter) : null;
    var endDate = Objects.nonNull(request.getDataFim()) ? request.getDataInicio().format(formatter) : null;

    var result = callProcedure(Processamento.PROCEDURE_PROCESSAR.getName())
        .declareParameters(
            new SqlParameter("p_dt_inicio", Types.VARCHAR),
            new SqlParameter("p_dt_fim", Types.VARCHAR),
            new SqlParameter("p_cc_id", Types.NUMERIC),
            new SqlParameter("p_tiprel_id", Types.NUMERIC),
            new SqlParameter("p_tipo", Types.VARCHAR),
            new SqlParameter("P_user_name", Types.VARCHAR),
            new SqlParameter("p_user_id", Types.NUMERIC),
            new SqlOutParameter("p_msg", Types.VARCHAR)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_dt_inicio", startDate)
                .addValue("p_dt_fim", endDate)
                .addValue("p_cc_id", request.getDireccaoId())
                .addValue("p_tiprel_id", request.getRelacionamentoId())
                .addValue("p_tipo", request.getTipo())
                .addValue("P_user_name", ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .map(Principal::getName)
                    .orElse("System"))
                .addValue("p_user_id", 0) // TODO 07/12/2025 17:48 validate this
        );

    return (String) result.get("p_msg");
  }

  private SimpleJdbcCall callProcedure(String procedureName) {
    return new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName(Processamento.PACKAGE.getName())
        .withProcedureName(procedureName);
  }

  @Getter
  private enum Processamento {

    PACKAGE("RH_PROCESSAMENTO_SALARIAL_DB"),
    PROCEDURE_ELIMINAR_CAB("EliminarCab"),
    ELIMINAR_PROCESSAMENTO("ELIMINAR_PROCESSAMENTO"),
    PROCEDURE_PROCESSAR("PROCESSAR");

    private final String name;

    Processamento(String name) {
      this.name = name;
    }
  }

  private enum StatusProcessamento {
    VALIDADO,
    PROCESSADO,
    ERRO_PROCESSAMENTO,
    VALIDADO_PROVISORIO,
    VALIDADO_DEFINITIVO,
    CABIMENTADO
  }
}
