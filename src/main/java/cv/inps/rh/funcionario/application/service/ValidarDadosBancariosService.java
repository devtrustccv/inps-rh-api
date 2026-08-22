package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosBancariosCommand;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosBancariosMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidarDadosBancariosService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDadosBancariosService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public SuccessResponseDTO executar(ValidarDadosBancariosCommand command) {

    var dto = command.getValidardadosbancarios();
    var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    // CORRIGIR (checker devolve ao maker): dados bancários pendentes P -> C e validação P -> C, SEM
    // aplicar payload. O maker corrige e reenvia por este mesmo endpoint com validar=null (C -> P).
    // Espelha o ciclo do contrato / mobilidade / carreira. Âncora = funcionario.uuid (referencia_uuid
    // gravado no registo dos dados bancários).
    if (EstadoValidacao.CORRIGIR.equals(estadoValidacao)) {
      if (!funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.DADOS_BANCARIOS)) {
        throw IgrpResponseStatusException.badRequest("Não há dados bancários pendentes para devolver para correção.");
      }
      funcionarioRules.devolverParaCorrecao(funcionario.getUuid(), Estado.P, Referencia.DADOS_BANCARIOS);
      funcionario.getDadosBancarios().stream()
          .filter(b -> b != null && b.getEstado() == Estado.P)
          .forEach(b -> b.setEstado(Estado.C));
      funcionarioEntityRepository.saveAndFlush(funcionario);
      LOGGER.info("[CORRIGIR] DADOS_BANCARIOS devolvidos para correção (funcionario={}).", funcionario.getUuid());
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Dados bancários devolvidos para correção.", List.of());
    }

    var dadosBancariosReqDTO = dto.getDadosBancarios();

    // Vínculo ativo do colaborador (para saber se tem salário → NIB obrigatório)
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    Long tipoVinculoId = (tipoRelAtual != null
        && tipoRelAtual.getContrVinculoId() != null
        && tipoRelAtual.getContrVinculoId().getVinculoId() != null)
        ? tipoRelAtual.getContrVinculoId().getVinculoId().getId()
        : null;

    // Maker reenvia a correção (C -> P): existe validação por corrigir. 'validar' tem de vir nulo —
    // um registo em correção não pode ser validado antes de reenviado.
    boolean estaPorCorrigir = funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.DADOS_BANCARIOS);
    if (estaPorCorrigir && estadoValidacao != null) {
      throw IgrpResponseStatusException.badRequest(
          "Dados bancários em correção: não podem ser validados. Corrija e reenvie primeiro.");
    }

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

    // Maker reenviou a correção: aplica as edições (sync acima) e reabre C -> P. Os itens que o sync
    // não marcou P (não alterados) são repostos aqui; os alterados já vêm em P do próprio sync.
    if (estaPorCorrigir) {
      funcionario.getDadosBancarios().stream()
          .filter(b -> b != null && b.getEstado() == Estado.C)
          .forEach(b -> b.setEstado(Estado.P));
      funcionarioRules.reabrirParaValidacao(funcionario.getUuid(), Referencia.DADOS_BANCARIOS);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Dados bancários corrigidos e reenviados para validação.", List.of());
    }

    if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      var mensagem = (estadoValidacao == EstadoValidacao.SIM)
          ? "Dados bancarios validados."
          : "Dados bancarios rejeitados.";
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
    }

    boolean temPendentesParaValidar = funcionario.getDadosBancarios().stream()
        .anyMatch(b -> b != null && b.getEstado() == Estado.P);

    if (!temPendentesParaValidar) {
      funcionarioEntityRepository.save(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Dados bancarios actualizados.", List.of());
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

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Dados bancarios actualizados.", List.of());

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
