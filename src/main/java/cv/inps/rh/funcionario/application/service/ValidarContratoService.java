package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final EntityManager entityManager;
  private final ColaboradorValidationRules colaboradorValidationRules;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ReconciliacaoMovimentoVinculoService reconciliacaoMovimentoVinculoService;

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
    // TODO(guard I/E temporariamente desativado): if (contrato != null) funcionarioRules.garantirEditavel(contrato.getEstado());
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

    // Sincronizacao de subsidios/encargos so faz sentido para vinculos COM salario.
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());
      colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

      // tms dos movimentos FIXOS do vinculo — o sync so mexe nos manuais, protege os fixos.
      var vinculoId = dadosContratuais.getTipoVinculoLaboralId();
      var tmsFixosRem = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "REM");
      var tmsFixosPag = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "PAG");
      var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
          dadosContratuais.getSubsidios(), funcionario, tmsFixosRem);
      var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
          dadosContratuais.getEncargosDescontos(), funcionario, tmsFixosPag);
      funcionario.getDefinicoesRenumeracoes().addAll(definicoesRemuneracoes);
      funcionario.getDefinicoesPagamentos().addAll(definicoesPagamentos);

      // Subsidios (def_remuneracoes) seguem o periodo e a moeda do contrato (o SubsidioReqDTO
      // nao tem datas nem moeda -> senao ficariam com NOW/moeda null). Encargos ja recebem o
      // default do contrato no ValidarDadosContratuaisService.
      if (contrato != null) {
        var ci = contrato.getDataInicio();
        var cf = contrato.getDataFim();
        funcionario.getDefinicoesRenumeracoes().forEach(r -> {
          if (r != null && r.getEstado() != Estado.E) {
            r.setDataInicio(ci); r.setDataFim(cf);
            if (r.getMoeda() == null) r.setMoeda(dadosContratuais.getMoeda());
          }
        });
      }
    }

    // Transicao de estado (A/I) deve acontecer SEMPRE que ha decisao de validacao,
    // independentemente de o vinculo ter salario — conforme a especificacao ("ao
    // validar, actualizar todas as tabelas associadas para estado='A'"). Antes estava
    // dentro do bloco flgSalario==1, pelo que estagios (flg_salario=0) nunca transitavam.
    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario, estado);
      // Subsidios/encargos MANUAIS nascem pendentes (P) e devem seguir a decisao de validacao
      // (SIM -> A, NAO -> I), tal como as restantes tabelas em mudarEstado. Sem isto ficavam em P
      // apos validar e nao seriam processados. Os movimentos FIXOS do vinculo sao tratados pelo
      // reconciliar (que os cria ja como A); aqui so se tocam os que estao pendentes.
      transicionarManuaisPendentes(funcionario, estado);
      if (estado == Estado.A) {
        // reconciliar os movimentos fixos do vinculo — SO na validacao positiva e SO com salario
        if (Objects.equals(1, paramVinculo.getFlgSalario())) {
          reconciliacaoMovimentoVinculoService.reconciliar(funcionario, contrato,
              dadosContratuais.getSalario(), dadosContratuais.getMoeda(),
              dadosContratuais.getDataInicio(), dadosContratuais.getDataFim());
        }
        // #7: os subsidios/encargos MANUAIS do contrato ANTERIOR (associados ao tiprel pai) NAO
        // transitam para o novo contrato — encerram-se (E). Sem isto, o associarNovos apanhava-os
        // (estado A) e o novo contrato herdava os subsidios/descontos do anterior. Os movimentos
        // FIXOS do vinculo ficam protegidos (sao tratados pelo reconciliar).
        fecharDefManuaisContratoAnterior(tiposRelacionamento, dadosContratuais.getTipoVinculoLaboralId());
        ordemServicoWriteService.criar(funcionario, tiposRelacionamento, dto.getTipoOrdemServico());
      } else {
        // Revert (validacao NAO): repoe o estado anterior ao REGISTO do novo contrato — o
        // contrato/tiprel/carreira/mob/regime ANTERIORES voltam a ativos e o novo fica inativo.
        // Regra nossa por logica (o DOSSIE so descreve "desvalidar -> estado=I"): sem isto o
        // colaborador ficaria sem contrato atual (o anterior foi fechado no registo).
        reverterRegistoNovoContrato(tiposRelacionamento);
      }
    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    // Numa REJEICAO (validar=NAO) nao se associam defs ao tiprel rejeitado — RH_T_TIPREL_REM_PAG
    // nao tem estado, logo a unica forma de nao os ter e nao criar a associacao.
    if (!EstadoValidacao.NAO.equals(dto.getValidar())) {
      tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);
    }

    // Só os def vigentes deste tiprel (estado coincide com o do tiprel) — exclui E/I que ficaram
    // associados (ex.: manuais substituídos pelo sync), tal como no getById/CarreiraReadService.
    var estadoTiprel = tiposRelacionamento.getEstado();
    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId())
        .stream().filter(r -> r.getEstado() == estadoTiprel).toList();
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId())
        .stream().filter(p -> p.getEstado() == estadoTiprel).toList();


    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes));

  }


  /** Subsidios (def_remuneracoes) e encargos (def_pagamentos) manuais pendentes seguem a validacao. */
  private void transicionarManuaisPendentes(FuncionarioEntity funcionario, Estado estado) {
    if (funcionario.getDefinicoesRenumeracoes() != null)
      funcionario.getDefinicoesRenumeracoes().stream()
          .filter(r -> r != null && r.getEstado() == Estado.P)
          .forEach(r -> r.setEstado(estado));
    if (funcionario.getDefinicoesPagamentos() != null)
      funcionario.getDefinicoesPagamentos().stream()
          .filter(p -> p != null && p.getEstado() == Estado.P)
          .forEach(p -> p.setEstado(estado));
  }

  /**
   * #7 — encerra (E) os subsidios/encargos MANUAIS associados ao tiprel do contrato ANTERIOR (o
   * tiprel pai). Nao devem transitar para o novo contrato; ficam apenas os do DTO do novo contrato
   * + os movimentos FIXOS do vinculo (tratados pelo reconciliar). Os fixos ficam protegidos via
   * tmsFixos para nao serem encerrados por engano.
   */
  private void fecharDefManuaisContratoAnterior(TiposRelacionamentoEntity novoTiprel, Long vinculoId) {
    var antigo = novoTiprel.getTiprelId();
    if (antigo == null) return;
    var tmsFixosRem = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "REM");
    var tmsFixosPag = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "PAG");
    funcionarioRules.getRemuneracoesAssociadosAtivos(antigo.getId()).stream()
        .filter(r -> r.getTmId() == null || !tmsFixosRem.contains(r.getTmId().getId()))
        .forEach(r -> r.setEstado(Estado.E));
    funcionarioRules.getPagamentosDescontosAssociadosAtivos(antigo.getId()).stream()
        .filter(p -> p.getTmId() == null || !tmsFixosPag.contains(p.getTmId().getId()))
        .forEach(p -> p.setEstado(Estado.E));
  }

  /**
   * Revert do registo de Novo Contrato numa validacao NEGATIVA: o novo tiprel deixa de ser o atual
   * e o contrato/tiprel/carreira/mobilidade/regime ANTERIORES (fechados no registo) voltam a ativos.
   * Os registos do NOVO contrato ja foram para 'I' via mudarEstado.
   */
  private void reverterRegistoNovoContrato(TiposRelacionamentoEntity novoTiprel) {
    novoTiprel.setEstActAdm(0);
    var antigo = novoTiprel.getTiprelId();
    if (antigo == null) return;
    antigo.setEstActAdm(1);
    antigo.setDataFim(null);
    if (antigo.getContrVinculoId() != null) antigo.getContrVinculoId().setEstado(Estado.A);
    if (antigo.getCarreiraId() != null) antigo.getCarreiraId().setDataFim(null);
    if (antigo.getMobId() != null) antigo.getMobId().setDataFim(null);
    if (antigo.getRegimeId() != null) antigo.getRegimeId().setDataFim(null);
  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);
      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contratoHistoricoWriteService.transicionarEstado(contrato, estado);
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
