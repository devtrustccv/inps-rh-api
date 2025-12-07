package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  // TODO 07/12/2025 15:19 validate the parameters for the procedure call actions

  private static final String PACKAGE = "RH_PROCESSAMENTO_SALARIAL_DB";

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DataSource dataSource;

  public void removerFuncionariosProcessados(List<String> funcionariosIds) {

    var ids = funcionariosIds.stream().map(UUID::fromString).toList();

    var funcionarios = funcionarioEntityRepository.findAllByUuidIn(ids);
    if (funcionarios.size() != funcionariosIds.size())
      throw IgrpResponseStatusException.badRequest("Funcionários não encontrados");

    for (var funcionario : funcionarios) {

      if (!funcionario.getEstado().equals(Estado.A))
        throw IgrpResponseStatusException.badRequest("Funcionário <%s> não está ativo".formatted(funcionario.getNome()));

      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      if (!tipoRelacionamentoAtual.getEstActAdm().equals(1))
        throw IgrpResponseStatusException.badRequest("O vínculo do colaborador <%s> não está activo!".formatted(funcionario.getNome()));

      // TODO 06/12/2025 17:00 validate this part from remuneracoes
      funcionario.getDefinicoesRenumeracoes().forEach(salario -> {
        if (!salario.getEstado().equals(Estado.A))
          throw IgrpResponseStatusException.badRequest("O salário do colaborador <%s> não está activo!".formatted(funcionario.getNome()));
      });

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

    processes.forEach(p -> callProcedure("EliminarProc").execute(Map.of("p_proc_id", p.getId())));
  }

  public void validar(List<Long> processamentoIds) {

    var processesThatCanNotBeValidated = new ArrayList<Long>();

    var processes = processamentoSalarialEntityRepository.findAllById(processamentoIds);
    processes.forEach(process -> {
      if (!process.getEstado().equals(ProcessamentoSalarialAction.VALIDAR.getCode()))
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

    // TODO 07/12/2025 15:19 validate this parameters
    processes.forEach(p -> callProcedure("CabimentarProc")
        .execute(
            Map.of(
                "p_qnt", p.getId(),
                "p_proc_sal_id", p.getId(),
                "p_ano_orcamento", p.getId()
            ))
    );

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

    processes.forEach(p -> callProcedure("EliminarCab").execute(Map.of("p_cab_id", p.getCab1Id())));
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

    // TODO 07/12/2025 15:19 validate this parameters
    processes.forEach(p -> callProcedure("AutorizarCab")
        .execute(
            Map.of(
                "p_qnt", p.getId(),
                "p_cabimento_id", p.getId()
            ))
    );
  }

  private SimpleJdbcCall callProcedure(String procedureName) {
    return new SimpleJdbcCall(dataSource)
        .withCatalogName(PACKAGE)
        .withProcedureName(procedureName);
  }
}
