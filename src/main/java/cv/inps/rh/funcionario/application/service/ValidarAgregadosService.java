package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosFamiliaresCommand;
import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FamiliarMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarAgregadosService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FamiliarMapper familiarMapper;
  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public ValidarAgregadosDependentesDTO executar(ValidarDadosFamiliaresCommand command) {

    var dto = command.getValidaragregadosdependentes();
    var agregadoDependenteReqDTO = dto.getFamiliares();
    //var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    /*boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.FAMILIA);

    // 1) Se tem pendentes mas não enviou validar → erro
    if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados de agregados, por favor validar"
      );
    }*/

    colaboradorValidationRules.verificarDuplicidadeFamiliares(agregadoDependenteReqDTO, funcionario.getFamiliares());

    var familiares = familiarMapper
        .syncFamiliares(funcionario.getFamiliares(), agregadoDependenteReqDTO, funcionario, Estado.A);
    funcionario.setFamiliares(familiares);

    /*if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }*/

    /*var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var validacao = contratuaisEntityMapper
        .toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.FAMILIA.name(), Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);

    funcionario.getValidacoes().add(validacao);*/

    funcionarioEntityRepository.saveAndFlush(funcionario);

    /*validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(
            funcionario.getUuid(),
            Estado.P,
            TipoAcao.UPDATE.name(),
            Referencia.FAMILIA.name()
        )
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });*/

    return dto;


  }

  /*private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado novoEstado) {
    if (funcionarioEntity == null) return;

    var familiares = funcionarioEntity.getFamiliares();
    if (familiares != null) {
      familiares.stream()
          .filter(f -> f != null && f.getEstado() == Estado.P)
          .forEach(f -> f.setEstado(novoEstado));
    }

    var validacaoPendente =
        funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.FAMILIA);
    validacaoPendente.ifPresent(v -> v.setEstado(novoEstado));

  }*/
}
