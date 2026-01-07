package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.processamento.application.dto.ProcessamentoSalarioRequestDTO;
import cv.inps.rh.processamento.domain.service.processamentosalarial.api.ProcessarSalarioApi;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessamentoSalarialWriteService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ProcessarSalarioApi processarSalarioApi;
  private final DataSource dataSource;

  public void removerFuncionariosProcessados(List<String> funcionariosIds) {

    var ids = funcionariosIds.stream().map(UUID::fromString).toList();

    var funcionarios = funcionarioEntityRepository.findAllByUuidIn(ids);
    if (funcionarios.size() != funcionariosIds.size())
      throw IgrpResponseStatusException.badRequest("Funcionários não encontrados");

    for (var funcionario : funcionarios) {

      if (!funcionario.getEstado().equals(Estado.A))
        throw IgrpResponseStatusException.badRequest("Funcionário <%s> não está ativo".formatted(funcionario.getNome()));

      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      if (!tipoRelacionamentoAtual.getEstActAdm().equals(1))
        throw IgrpResponseStatusException.badRequest("O vínculo do colaborador <%s> não está activo!".formatted(funcionario.getNome()));

      tipoRelacionamentoAtual.setFlgProcessa("0");
      tiposRelacionamentoEntityRepository.save(tipoRelacionamentoAtual);
    }
  }

  public void eliminarProcessamento(List<Long> processamentoIds) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.ELIMINAR_PROCESSAMENTO.getCode()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'PROV'", illegalProcesses);

    processes.forEach(p -> {
      var call = callProcedure(Processamento.PROCEDURE_ELIMINAR_PROC.getName());
      call.execute(Map.of("p_proc_id", p.getId()));
    });
  }

  public void validar(List<Long> processamentoIds) {

    var processesThatCanNotBeValidated = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!ProcessamentoSalarialAction.VALIDAR.getCode().equals(process.getEstado()))
        processesThatCanNotBeValidated.add(process.getId());
      else
        process.setEstado("VALIDADO");
    });

    if (!processesThatCanNotBeValidated.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'PROV'", processesThatCanNotBeValidated);

    processamentoSalarialEntityRepository.saveAll(processes);
  }

  public void cabimentar(List<Long> processamentoIds) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.CABIMENTAR.getCode()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'VALIDADO'", illegalProcesses);

    var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    processes.forEach(p -> {
      var date = p.getDataProcDefinitivo().format(formatter);
      var response = processarSalarioApi.processarCabimento(p.getId().toString(), date);
      LOGGER.info("Cabimentar Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao processar cabimento", response.content().issue().diagnostics());
    });
  }

  public void eliminarCabimento(List<Long> processamentoIds) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.ELIMINAR_CABIMENTO.getCode()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'DEV'", illegalProcesses);

    processes.forEach(p -> {
      var call = callProcedure(Processamento.PROCEDURE_ELIMINAR_CAB.getName());
      call.execute(Map.of("p_cab_id", p.getCab1Id()));
    });
  }

  public void autorizar(List<Long> processamentoIds) {

    var illegalProcesses = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.AUTORIZAR.getCode()))
        illegalProcesses.add(process.getId());
    });

    if (!illegalProcesses.isEmpty())
      throw IgrpResponseStatusException.badRequest("Existem processos que não se encontram no estado 'CABIMENTADO'", illegalProcesses);

    processes.forEach(p -> {
      var response = processarSalarioApi.autorizarSalario(p.getCab1Id().toString(), "SIM");
      LOGGER.info("Autorizar Salario Response: {}", response);
      if (response.content().issue().code() != 200)
        throw IgrpResponseStatusException.badRequest("Erro ao autorizar salario", response.content().issue().diagnostics());
    });
  }

  public void processarSalario(ProcessamentoSalarioRequestDTO request) {

    callProcedure(Processamento.PROCEDURE_PROCESSAR.getName())
        .declareParameters(
            new SqlParameter("p_dt_inicio", Types.VARCHAR),
            new SqlParameter("p_dt_fim", Types.VARCHAR),
            new SqlParameter("p_cc_id", Types.NUMERIC),
            new SqlParameter("p_tiprel_id", Types.NUMERIC),
            new SqlParameter("p_tipo", Types.VARCHAR),
            new SqlParameter("P_user_name", Types.VARCHAR),
            new SqlParameter("p_user_id", Types.NUMERIC)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("p_dt_inicio", request.getDataInicio())
                .addValue("p_dt_fim", request.getDataFim())
                .addValue("p_cc_id", request.getDireccaoId())
                .addValue("p_tiprel_id", request.getRelacionamentoId())
                .addValue("p_tipo", request.getTipo())
                .addValue("P_user_name", "demo@demo.com") // TODO 07/12/2025 17:48 validate this
                .addValue("p_user_id", 0) // TODO 07/12/2025 17:48 validate this
        );
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
    PROCEDURE_ELIMINAR_PROC("EliminarProc"),
    PROCEDURE_PROCESSAR("PROCESSAR");

    private final String name;

    Processamento(String name) {
      this.name = name;
    }
  }
}
