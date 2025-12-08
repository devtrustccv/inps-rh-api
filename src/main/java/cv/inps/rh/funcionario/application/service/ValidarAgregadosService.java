package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosFamiliaresCommand;
import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FamiliarMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarAgregadosService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FamiliarMapper familiarMapper;

  @Transactional
  public ValidarAgregadosDependentesDTO executar(ValidarDadosFamiliaresCommand command) {

    var dto = command.getValidaragregadosdependentes();
    var agregadoDependenteReqDTO = dto.getFamiliares();
    var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario, "UPDATE", "FAMILIA");

    // 1) Se tem pendentes mas não enviou validar → erro
    if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados pessoais, por favor validar"
      );
    }

    var familiares = familiarMapper.syncFamiliares(funcionario.getFamiliares(), agregadoDependenteReqDTO);
    funcionario.setFamiliares(familiares);

    if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var validacao = contratuaisEntityMapper
        .toValidacaoInsert("UPDATE", "FAMILIA", Estado.P);

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);

    funcionario.setValidacoes(java.util.List.of(validacao));

    var saved = funcionarioEntityRepository.save(funcionario);

    validacaoEntityRepository.findById(validacao.getId())
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });

    return dto;


  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado novoEstado) {
    if (funcionarioEntity == null) return;

    var familiares = funcionarioEntity.getFamiliares();
    if (familiares != null) familiares.forEach(f -> { if (f != null) f.setEstado(novoEstado); });

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "FAMILIA".equals(v.getReferenciaName()) && "UPDATE".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(novoEstado));

  }
}
