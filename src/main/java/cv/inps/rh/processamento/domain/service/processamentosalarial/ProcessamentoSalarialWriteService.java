package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.constants.TipoValidacaoProcessamentoSalarial;
import cv.inps.rh.processamento.application.dto.ProcessamentoSalarioRequestDTO;
import cv.inps.rh.shared.application.events.NotificationEvent;
import cv.inps.rh.shared.application.services.IAMUserProfileService;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.events.EventPublisher;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialWriteService.class);

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final DataSource dataSource;
  private final JdbcTemplate jdbcTemplate;
  private final ApplicationAuditorAware applicationAuditorAware;
  private final IAMUserProfileService iamUserProfileService;
  private final EventPublisher eventPublisher;

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

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    var username = applicationAuditorAware.getCurrentSubjectName();
    var updatedRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(statusPesquisa.name()),
        statusRegisto.name(),
        isDefinitivo ? null : username,
        isDefinitivo ? username : null,
        null,
        null
    );
    if (updatedRows != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado %s".formatted(statusPesquisa.name()));

    publicarNotificacoes(
        processes,
        isDefinitivo ? ProcessamentoSalarialEntity::getUserValidProv : ProcessamentoSalarialEntity::getCreatedBy,
        isDefinitivo ? "Validação definitiva" : "Validação provisória"
    );
  }

  @Transactional
  public void retroceder(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    var provisionalRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(StatusProcessamento.VALIDADO_PROVISORIO.name()),
        StatusProcessamento.PROCESSADO.name(),
        null,
        null,
        null,
        null
    );
    var definitiveRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(StatusProcessamento.VALIDADO_DEFINITIVO.name()),
        StatusProcessamento.VALIDADO_PROVISORIO.name(),
        null,
        null,
        null,
        null
    );
    if (provisionalRows + definitiveRows != ids.size())
      throw IgrpResponseStatusException.badRequest("Processos a serem retrocedidos devem estar nos estados de validaçao provisório ou definitivo");

    publicarNotificacoes(
        processes,
        process -> StatusProcessamento.VALIDADO_DEFINITIVO.name().equals(process.getEstado())
            ? process.getUserValidDef()
            : process.getUserValidProv(),
        "Retrocesso"
    );
  }

  @Transactional
  public void cabimentar(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    var username = applicationAuditorAware.getCurrentSubjectName();
    var updatedRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(StatusProcessamento.VALIDADO_DEFINITIVO.name()),
        StatusProcessamento.CABIMENTADO.name(),
        null,
        null,
        username,
        null
    );
    if (updatedRows != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram Validados definitivamente");

    publicarNotificacoes(
        processes,
        ProcessamentoSalarialEntity::getUserValidDef,
        "Cabimento"
    );
  }

  @Transactional
  public void autorizar(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    var username = applicationAuditorAware.getCurrentSubjectName();
    var updatedRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(StatusProcessamento.CABIMENTADO.name()),
        StatusProcessamento.AUTORIZADO.name(),
        null,
        null,
        null,
        username
    );
    if (updatedRows != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado Cabimentado");

    publicarNotificacoes(
        processes,
        ProcessamentoSalarialEntity::getUserCabimento,
        "Autorização"
    );
  }

  @Transactional
  public void eliminarCabimento(List<Long> ids) {

    var processes = processamentoSalarialEntityRepository.findAllById(ids);
    var updatedRows = processamentoSalarialEntityRepository.atualizarEstadoEUtilizadores(
        ids,
        List.of(StatusProcessamento.CABIMENTADO.name()),
        StatusProcessamento.VALIDADO_DEFINITIVO.name(),
        null,
        null,
        null,
        null
    );
    if (updatedRows != ids.size())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado Cabimentado");

    publicarNotificacoes(
        processes,
        ProcessamentoSalarialEntity::getUserCabimento,
        "Eliminação do cabimento"
    );
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

      stmt.setString(6, applicationAuditorAware.getCurrentSubjectName());

      stmt.setInt(7, 0);

      stmt.registerOutParameter(8, Types.VARCHAR);

      stmt.execute();

      return stmt.getString(8);

    } catch (SQLException ex) {
      throw new RuntimeException("Error executing PROCESSAR", ex);
    }
  }

  private void publicarNotificacoes(
      List<ProcessamentoSalarialEntity> processes,
      Function<ProcessamentoSalarialEntity, String> previousUserResolver,
      String action
  ) {
    var processIdsByUser = new LinkedHashMap<String, List<Long>>();

    for (var process : processes) {
      var previousUser = previousUserResolver.apply(process);
      if (StringUtils.hasText(previousUser)) {
        processIdsByUser.computeIfAbsent(previousUser, ignored -> new ArrayList<>())
            .add(process.getId());
      }
    }

    if (processIdsByUser.isEmpty()) {
      LOGGER.warn("No previous users found for salary processing notification: action={}", action);
      return;
    }

    var profilesByUser = iamUserProfileService.resolverPerfis(processIdsByUser.keySet());
    var notifications = new ArrayList<NotificationEvent>();

    processIdsByUser.forEach((userId, processIds) -> {
      var profile = profilesByUser.get(userId);
      if (profile == null || !StringUtils.hasText(profile.getEmail())) {
        LOGGER.warn("No email found for salary processing notification: user={} action={}", userId, action);
        return;
      }

      var recipientName = StringUtils.hasText(profile.getFullName())
          ? profile.getFullName()
          : profile.getUsername();
      var subject = "Processamento salarial - " + action;
      var body = "Olá %s,%n%nFoi executada a ação \"%s\" nos processamentos salariais %s."
          .formatted(recipientName, action, processIds);

      notifications.add(NotificationEvent.text(profile.getEmail(), subject, body));
    });

    if (notifications.isEmpty())
      return;

    notifications.forEach(notification -> {
      try {
        eventPublisher.publish(notification);
      } catch (RuntimeException exception) {
        LOGGER.error("Unable to publish salary processing notification to {}", notification.recipient(), exception);
      }
    });
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
