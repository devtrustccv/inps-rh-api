package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidarContratoService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ContratoMapper contratoMapper;

  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final FuncionarioRules funcionarioRules;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final EntityManager entityManager;

  @Transactional
  public ResponseEntity<DadosContratuaisRespDTO> validar(ValidarContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var dadosContratuais = dto.getDadosContratuais();

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente para o tipo de acao: INSERT e referencia: CONTRATO");
    }

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dadosContratuais);

    var contrato = tiposRelacionamento.getContrVinculoId();
    contratoMapper.toUpdateEntity(contrato, dadosContratuais);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dadosContratuais);

    var carreira = tiposRelacionamento.getCarreiraId() != null ? tiposRelacionamento.getCarreiraId() : null;
    if (carreira != null) {
      carreiraMapper.toUpdateEntity(carreira, dadosContratuais);
    }

    var situacaoLaboral = tiposRelacionamento.getSituacLaboralId();
    dadosContratuaisMapper.toUpdateSituacaoLaboral(situacaoLaboral, dadosContratuais);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dadosContratuais);

    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
          dadosContratuais.getSubsidios());
      var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
          dadosContratuais.getEncargosDescontos());
      funcionario.getDefinicoesRenumeracoes().addAll(definicoesRemuneracoes);
      funcionario.getDefinicoesPagamentos().addAll(definicoesPagamentos);

      if (dto.getValidar() != null) {
        var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
        mudarEstado(funcionario, estado);
      }
    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

     associarPagamentosERemuneracoes(saved, tiposRelacionamento);

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId());


    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes));

  }

  private void associarPagamentosERemuneracoes(FuncionarioEntity saved, TiposRelacionamentoEntity tiposRelacionamento) {
    // =========================
// Associações para remunerações
// =========================
    List<TipoRelRemPagEntity> listRemunTipRel = new ArrayList<>();
    if (saved.getDefinicoesRenumeracoes() != null && !saved.getDefinicoesRenumeracoes().isEmpty()) {

      Set<Long> remIdsJaProcessados = new HashSet<>(); // passo 1: deduplicação local

      for (var rem : saved.getDefinicoesRenumeracoes()) {

        // passo 2: verifica se o estado é válido
        boolean estadoValido = rem.getEstado().equals(Estado.A) || rem.getEstado().equals(Estado.P);
        if (!estadoValido) continue;

        // passo 3: verifica se já processamos este ID no Set local
        boolean naoProcessadoAinda = remIdsJaProcessados.add(rem.getId());
        if (!naoProcessadoAinda) continue;

        // passo 4: verifica se a associação já existe no banco
        boolean associacaoExiste = tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tiposRelacionamento, rem);
        if (associacaoExiste) continue;

        // passo 5: criar nova associação
        TipoRelRemPagEntity assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tiposRelacionamento);
        assoc.setRemId(rem);
        assoc.setPagId(null);

        // passo 6: adicionar à lista de inserção
        listRemunTipRel.add(assoc);
      }

      // passo 7: salvar todas as novas associações de uma vez
      if (!listRemunTipRel.isEmpty()) {
        tipoRelRemPagEntityRepository.saveAll(listRemunTipRel);
      }
    }

// =========================
// Associações para pagamentos
// =========================
    List<TipoRelRemPagEntity> listPagTipRel = new ArrayList<>();
    if (saved.getDefinicoesPagamentos() != null && !saved.getDefinicoesPagamentos().isEmpty()) {

      Set<Long> pagIdsJaProcessados = new HashSet<>(); // passo 1: deduplicação local

      for (var pag : saved.getDefinicoesPagamentos()) {

        // passo 2: verifica se o estado é válido
        boolean estadoValido = pag.getEstado().equals(Estado.A) || pag.getEstado().equals(Estado.P);
        if (!estadoValido) continue;

        // passo 3: verifica se já processamos este ID no Set local
        boolean naoProcessadoAinda = pagIdsJaProcessados.add(pag.getId());
        if (!naoProcessadoAinda) continue;

        // passo 4: verifica se a associação já existe no banco
        boolean associacaoExiste = tipoRelRemPagEntityRepository.existsByTiprelIdAndPagId(tiposRelacionamento, pag);
        if (associacaoExiste) continue;

        // passo 5: criar nova associação
        TipoRelRemPagEntity assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tiposRelacionamento);
        assoc.setPagId(pag);
        assoc.setRemId(null);

        // passo 6: adicionar à lista de inserção
        listPagTipRel.add(assoc);
      }

      // passo 7: salvar todas as novas associações de uma vez
      if (!listPagTipRel.isEmpty()) {
        tipoRelRemPagEntityRepository.saveAll(listPagTipRel);
      }
    }

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);
      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contrato.setEstado(estado);
        contrato.getSituacoesLaborais().stream()
            .filter(o -> o.getEstado() == Estado.P)
            .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));
      }

      var mob = tr.getMobId();
      if (mob != null)
        mob.setEstado(estado);

      var carreira = tr.getCarreiraId() != null ? tr.getCarreiraId() : null;
      if (carreira != null)
        carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null)
        regime.setEstado(estado);

      var situacaoLaboral = tr.getSituacLaboralId();
      if (situacaoLaboral != null)
        situacaoLaboral.setEstado(estado);

    }

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.INSERT, Referencia.CONTRATO)
        .ifPresent(v -> v.setEstado(estado));

  }
}
