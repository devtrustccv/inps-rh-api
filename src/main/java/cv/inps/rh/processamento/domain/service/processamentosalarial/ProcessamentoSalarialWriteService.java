package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.constants.TipoValidacaoProcessamentoSalarial;
import cv.inps.rh.processamento.application.dto.ProcessamentoSalarioRequestDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.api.ProcessarSalarioApi;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialWriteService.class);

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final ProcessarSalarioApi processarSalarioApi;
  private final ProcessamentoSalarialHelper processamentoSalarialHelper;
  private final DataSource dataSource;
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public void removerFuncionariosProcessados(List<String> funcionariosIds) {

    var ids = funcionariosIds.stream().map(UUID::fromString).toList();

    var relations = tiposRelacionamentoEntityRepository.findRelacionamentosAtuaisByFuncionarioUuids(ids);
    if (relations.isEmpty())
      return;

    relations.forEach(r -> r.setFlgProcessa(0));
    tiposRelacionamentoEntityRepository.saveAll(relations);
  }

  @Transactional
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
          processingIds,
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

  @Transactional
  public void validar(List<Long> ids, TipoValidacaoProcessamentoSalarial tipoValidacao) {

    if (Objects.isNull(tipoValidacao))
      throw IgrpResponseStatusException.badRequest("Para validar deve indicar o tipo de validacao: [DEFINITIVO, PROVISORIO]");

    var isDefinitivo = TipoValidacaoProcessamentoSalarial.DEFINITIVO.equals(tipoValidacao);
    var statusPesquisa = isDefinitivo ? StatusProcessamento.VALIDADO_PROVISORIO : StatusProcessamento.PROCESSADO;
    var statusRegisto = isDefinitivo ? StatusProcessamento.VALIDADO_DEFINITIVO : StatusProcessamento.VALIDADO_PROVISORIO;

    var processes = processamentoSalarialEntityRepository.findAllByIdInAndEstadoIn(
        ids,
        List.of(statusPesquisa.name())
    );
    if (processes.size() != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado %s".formatted(statusPesquisa.name()));

    processes.forEach(process -> process.setEstado(statusRegisto.name()));

    processamentoSalarialEntityRepository.saveAll(processes);
  }

  @Transactional
  public void retroceder(List<Long> ids) {

    var allowedStatusToBeRollback = List.of(
        StatusProcessamento.VALIDADO_PROVISORIO.name(),
        StatusProcessamento.VALIDADO_DEFINITIVO.name()
    );

    var processes = processamentoSalarialEntityRepository.findAllByIdInAndEstadoIn(ids, allowedStatusToBeRollback);
    if (processes.size() != ids.size())
      throw IgrpResponseStatusException.badRequest("Processos a serem retrocedidos devem estar nos estados de validaçao provisório ou definitivo");

    processes.forEach(process -> {
      var status = switch (StatusProcessamento.valueOf(process.getEstado())) {
        case VALIDADO_PROVISORIO -> StatusProcessamento.PROCESSADO.name();
        case VALIDADO_DEFINITIVO -> StatusProcessamento.VALIDADO_PROVISORIO.name();
        default ->
            throw new IllegalArgumentException("Invalid state for salary processing rollback!");
      };
      process.setEstado(status);
    });

    processamentoSalarialEntityRepository.saveAll(processes);
  }

  public void cabimentar(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllByIdInAndEstadoIn(
        ids,
        List.of(StatusProcessamento.VALIDADO_DEFINITIVO.name())
    );
    if (processes.size() != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram Validados definitivamente");

    processes.forEach(p -> {
     /* var date = p.getDataProcDefinitivo().format(DateFormatter.DATE);
      var response = processarSalarioApi.processarCabimento(p.getId().toString(), date);
      LOGGER.debug("Cabimentar Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao processar cabimento", response.content().issue().diagnostics());*/
      processamentoSalarialHelper.atualizarEstado(p.getId(), StatusProcessamento.CABIMENTADO.name());
    });
  }

  public void autorizar(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllByIdInAndEstadoIn(
        ids,
        List.of(StatusProcessamento.CABIMENTADO.name())
    );
    if (processes.size() != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado Cabimentado");

    processes.forEach(p -> {
     /* var response = processarSalarioApi.autorizarSalario(p.getCab1Id().toString(), "SIM");
      LOGGER.debug("Autorizar Salario Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao autorizar salario", response.content().issue().diagnostics());*/
      processamentoSalarialHelper.atualizarEstado(p.getId(), StatusProcessamento.AUTORIZADO.name());
    });
  }

  public void eliminarCabimento(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllByIdInAndEstadoIn(
        ids,
        List.of(StatusProcessamento.CABIMENTADO.name())
    );
    if (processes.size() != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado Cabimentado");

    processes.forEach(p -> {
     /* var response = processarSalarioApi.extornarCabimento(p.getCab1Id().toString());
      LOGGER.debug("Extornar Cabimento Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao eliminar cabimento", response.content().issue().diagnostics());*/
      processamentoSalarialHelper.atualizarEstado(p.getId(), StatusProcessamento.VALIDADO_DEFINITIVO.name());
    });
  }

  public String processarSalario(ProcessamentoSalarioRequestDTO request) {

    var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    var startDate = request.getDataInicio() != null
        ? request.getDataInicio().format(formatter)
        : null;

    var endDate = request.getDataFim() != null
        ? request.getDataFim().format(formatter)
        : null;

    var ccIds = request.getDireccaoId()
        .stream()
        .map(String::valueOf)
        .toArray(String[]::new);

    var procedure = "{ call RH_PROCESSAMENTO_SALARIAL_DB.PROCESSAR(?, ?, ?, ?, ?, ?, ?, ?) }";

    try (var connection = dataSource.getConnection()) {

      var stmt = connection.prepareCall(procedure).unwrap(OracleCallableStatement.class);
      stmt.setString(1, startDate);
      stmt.setString(2, endDate);
      stmt.setPlsqlIndexTable(
          3,
          ccIds,
          ccIds.length,
          ccIds.length,
          OracleTypes.VARCHAR,
          32767
      );

      if (request.getRelacionamentoId() == null) {
        stmt.setNull(4, Types.NUMERIC);
      } else {
        stmt.setLong(4, request.getRelacionamentoId());
      }

      stmt.setString(5, request.getTipo());

      stmt.setString(
          6,
          Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
              .map(Principal::getName)
              .orElse("System")
      );

      stmt.setInt(7, 0);

      stmt.registerOutParameter(8, Types.VARCHAR);

      stmt.execute();

      return stmt.getString(8);

    } catch (SQLException ex) {
      throw new RuntimeException("Error executing PROCESSAR", ex);
    }
  }

  private enum StatusProcessamento {
    VALIDADO,
    AUTORIZADO,
    PROCESSADO,
    ERRO_PROCESSAMENTO,
    VALIDADO_PROVISORIO,
    VALIDADO_DEFINITIVO,
    CABIMENTADO
  }
}
