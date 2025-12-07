package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.constants.ProcessamentoSalarialAction;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;
  private final FuncionarioRules funcionarioRules;

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

    // TODO 07/12/2025 15:19 implement this action

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

    // TODO 07/12/2025 15:19 implement this action

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

    // TODO 07/12/2025 15:19 implement this action

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

    // TODO 07/12/2025 15:19 implement this action
  }
}
