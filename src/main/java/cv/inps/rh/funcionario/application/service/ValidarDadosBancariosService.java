package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosBancariosCommand;
import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosBancariosMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
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
public class ValidarDadosBancariosService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public ValidarDadosBancariosDTO executar(ValidarDadosBancariosCommand command) {

    var dto = command.getValidardadosbancarios();
    var dadosBancariosReqDTO = dto.getDadosBancarios();
    var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    // Vínculo ativo do colaborador (para saber se tem salário → NIB obrigatório)
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    Long tipoVinculoId = (tipoRelAtual != null
        && tipoRelAtual.getContrVinculoId() != null
        && tipoRelAtual.getContrVinculoId().getVinculoId() != null)
        ? tipoRelAtual.getContrVinculoId().getVinculoId().getId()
        : null;

    boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.DADOS_BANCARIOS);

    // 1) Se tem pendentes mas não enviou validar → erro
    if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados bancarios, por favor validar");
    }

    var dadosBancarios = dadosBancariosMapper
    .syncBancarios(funcionario.getDadosBancarios(), dadosBancariosReqDTO, funcionario);
    funcionario.setDadosBancarios(dadosBancarios);

    // NIB obrigatório quando o vínculo tem salário — valida o estado efetivo (existentes + enviados)
    colaboradorValidationRules.validarNibObrigatorioSeSalarioEfetivo(tipoVinculoId, dadosBancarios);

    if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    boolean temPendentesParaValidar = funcionario.getDadosBancarios().stream()
        .anyMatch(b -> b != null && b.getEstado() == Estado.P);

    if (!temPendentesParaValidar) {
      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var validacao = contratuaisEntityMapper
        .toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.DADOS_BANCARIOS.name(), Estado.P);

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);
    validacao.setReferenciaUuid(funcionario.getUuid());

    funcionario.getValidacoes().add(validacao);

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(
            funcionario.getUuid(),
            Estado.P,
            TipoAcao.UPDATE.name(),
            Referencia.DADOS_BANCARIOS.name())
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });

    return dto;

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado novoEstado) {
    if (funcionarioEntity == null)
      return;

    var bancarios = funcionarioEntity.getDadosBancarios();

    if (bancarios != null) {
      bancarios.stream()
          .filter(b -> b != null && b.getEstado() == Estado.P)
          .forEach(b -> b.setEstado(novoEstado));
    }

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.DADOS_BANCARIOS)
        .ifPresent(v -> v.setEstado(novoEstado));
  }
}
