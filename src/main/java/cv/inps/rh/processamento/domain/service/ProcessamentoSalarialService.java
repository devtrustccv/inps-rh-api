package cv.inps.rh.processamento.domain.service;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
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


}
