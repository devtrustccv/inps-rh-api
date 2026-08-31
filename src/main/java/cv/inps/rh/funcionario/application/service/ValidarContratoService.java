package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoSalarioVinculo;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AlertaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ValidarContratoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoService.class);

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
  private final AlertaEntityRepository alertaEntityRepository;

  /** Tipo de alerta de conversão de contrato gerado pelo JOB (ver AlertaWriteService). */
  private static final String TIPO_ALERTA_CONVERSAO = "CONVERSAO_CONTRATO";

  @Transactional
  public ResponseEntity<SuccessResponseDTO> validar(ValidarContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    // Contrato alvo = o do tiprel atual (o novo contrato registado, ainda por validar/corrigir).
    var tiprelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var contratoAlvo = tiprelAtual != null ? tiprelAtual.getContrVinculoId() : null;

    // CORRIGIR (checker devolve ao maker): contrato pendente P -> C e validação P -> C, SEM aplicar
    // payload nem tocar nos def. O maker corrige e reenvia por este mesmo endpoint com validar=null
    // (C -> P). Espelha o ciclo do registo de colaborador / mobilidade / carreira.
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      if (contratoAlvo == null || contratoAlvo.getEstado() != Estado.P) {
        throw IgrpResponseStatusException.badRequest("Não há contrato pendente para devolver para correção.");
      }
      funcionarioRules.devolverParaCorrecao(contratoAlvo.getUuid(), contratoAlvo.getEstado(), Referencia.CONTRATO);
      mudarEstado(funcionario, Estado.C);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      LOGGER.info("[CORRIGIR] CONTRATO devolvido para correção (contrato={}).", contratoAlvo.getUuid());
      return ResponseEntity.ok(new SuccessResponseDTO(true, contratoAlvo.getUuid().toString(),
          "Contrato devolvido para correção.", List.of()));
    }

    // Maker corrige e reenvia (C -> P): 'validar' tem de vir nulo e tem de existir validação por corrigir.
    boolean estaPorCorrigir = contratoAlvo != null && contratoAlvo.getEstado() == Estado.C;
    if (estaPorCorrigir) {
      if (dto.getValidar() != null) {
        throw IgrpResponseStatusException.badRequest(
            "Contrato em correção: não pode ser validado. Corrija e reenvie primeiro.");
      }
      if (!funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.INSERT, Referencia.CONTRATO)) {
        throw IgrpResponseStatusException.badRequest("Este contrato não está por corrigir.");
      }
    }

    var dadosContratuais = dto.getDadosContratuais();

    // Mesma regra de datas do registo do Novo Contrato (DOSSIÊ): início pode ser no passado, mas
    // não no futuro — senão um contrato criado com data retroativa não passaria na validação.
    validarDadosContratuaisService.validar(dadosContratuais,
        ValidarDadosContratuaisService.RegraDataInicio.NAO_FUTURA);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "O funcionário '%s' não possui uma validação pendente de contrato."
              .formatted(funcionario.getNome()));
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

    // Novo contrato = TUDO NOVO (não é renovação): os def do contrato ANTERIOR não pertencem a este
    // fluxo. Numa validação POSITIVA, encerra-os por DATA_FIM mantendo 'A' (doc "Novo Contrato":
    // encerrar por DATA_FIM; o 'I' é só rejeição) e retira-os da coleção de trabalho — para que o
    // sync (eliminar/adicionar/atualizar), o reconciliar e o associarNovos, que iteram a coleção do
    // funcionário, só vejam os def do NOVO contrato. Corre ANTES do sync.
    if (EstadoValidacao.SIM.equals(dto.getValidar())) {
      encerrarEExcluirDefsContratoAnterior(funcionario, tiposRelacionamento);
    }

    // Sincronizacao de subsidios/encargos so faz sentido para vinculos COM salario — e NUNCA numa
    // REJEICAO (NAO): o sync elimina os def existentes que nao vem no DTO, o que numa rejeicao
    // apagaria os def do contrato ANTERIOR (que o revert vai reativar). No NAO nao se mexe nos def:
    // os do novo contrato (P) vao a I pelo transicionarManuaisPendentes.
    if (TipoSalarioVinculo.temSalario(paramVinculo.getFlgSalario())
        && !EstadoValidacao.NAO.equals(dto.getValidar())) {
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
      // Conversão via alerta (JOB Alerta / TRANSVERSAL 3.4.2 §2): fecha o alerta CONVERSAO_CONTRATO do
      // contrato ANTERIOR (o que foi convertido). SIM -> estado='I' (resolvido); NÃO -> flg_tratamento='N'
      // (volta à grelha). NO-OP num Novo Contrato normal (sem alerta de conversão sobre o contrato anterior).
      var tiprelAntigoConversao = tiposRelacionamento.getTiprelId();
      var contratoAntigoConversao = tiprelAntigoConversao != null ? tiprelAntigoConversao.getContrVinculoId() : null;
      marcarAlertaConversao(contratoAntigoConversao, EstadoValidacao.SIM.equals(dto.getValidar()));
      if (estado == Estado.A) {
        // Os def do contrato ANTERIOR já foram encerrados (DATA_FIM, mantendo 'A') e retirados da
        // coleção de trabalho acima (encerrarEExcluirDefsContratoAnterior). Por isso o reconciliar
        // cria fixos FRESCOS para o novo contrato (não vê os antigos) e o associarNovos só associa os
        // novos — separação limpa por contrato, SEM inativar/apagar os antigos (ficam visíveis no
        // getById do contrato anterior).
        // reconciliar os movimentos fixos do vinculo — SO na validacao positiva e SO com salario
        if (TipoSalarioVinculo.temSalario(paramVinculo.getFlgSalario())) {
          // Melhoria 2.1: vínculo sem carreira + SIM_PCCS → reafirma o escalão no tiprel e usa o valor
          // do escalão como salário base a reconciliar (senão usa o salário do formulário).
          var escalaoAplicado = colaboradorValidationRules.aplicarEscalaoTiprelSemCarreira(
              tiposRelacionamento, tiposRelacionamento.getCarreiraId(), paramVinculo,
              dadosContratuais.getEscalaoReferenciaId());
          var salarioReconciliar = escalaoAplicado != null && escalaoAplicado.getValor() != null
              ? escalaoAplicado.getValor() : dadosContratuais.getSalario();
          reconciliacaoMovimentoVinculoService.reconciliar(funcionario, contrato,
              salarioReconciliar, dadosContratuais.getMoeda(),
              dadosContratuais.getDataInicio(), dadosContratuais.getDataFim());
        }
        ordemServicoWriteService.criar(funcionario, tiposRelacionamento, dto.getTipoOrdemServico());
      } else {
        // Revert (validacao NAO): repoe o estado anterior ao REGISTO do novo contrato — o
        // contrato/tiprel/carreira/mob/regime ANTERIORES voltam a ativos e o novo fica inativo.
        // Regra nossa por logica (o DOSSIE so descreve "desvalidar -> estado=I"): sem isto o
        // colaborador ficaria sem contrato atual (o anterior foi fechado no registo).
        reverterRegistoNovoContrato(tiposRelacionamento);
      }
    } else if (estaPorCorrigir) {
      // Maker reenviou a correção: C -> P (volta à fila de validação). Sem consolidar nem ordem de
      // serviço — isso só na validação positiva (SIM). Os manuais editados mantêm-se P.
      funcionarioRules.reabrirParaValidacao(contratoAlvo.getUuid(), Referencia.CONTRATO);
      mudarEstado(funcionario, Estado.P);
    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    // Numa REJEICAO (validar=NAO) nao se associam defs ao tiprel rejeitado — RH_T_TIPREL_REM_PAG
    // nao tem estado, logo a unica forma de nao os ter e nao criar a associacao.
    if (!EstadoValidacao.NAO.equals(dto.getValidar())) {
      tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);
    }

    // Só os def vigentes deste tiprel (estado coincide com o do tiprel) — exclui E/I que ficaram
    // associados (ex.: manuais substituídos pelo sync), tal como no getById/CarreiraReadService.
    var mensagem = estaPorCorrigir
        ? "Contrato corrigido e reenviado para validação."
        : EstadoValidacao.SIM.equals(dto.getValidar())
            ? "Contrato validado."
            : "Contrato actualizado.";
    return ResponseEntity.ok(new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of()));

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
   * Inativa (I) TODOS os def (subsidios/encargos manuais + fixos) associados ao tiprel do contrato
   * ANTERIOR (o tiprel pai). Chamado ANTES do reconciliar na validacao positiva de um novo contrato:
   * o contrato anterior fica totalmente encerrado, e o reconciliar cria fixos FRESCOS para o novo
   * contrato em vez de reutilizar os do anterior (que de outra forma ficavam ativos e partilhados
   * entre os dois contratos).
   */
  private void encerrarEExcluirDefsContratoAnterior(FuncionarioEntity funcionario, TiposRelacionamentoEntity novoTiprel) {
    var antigo = novoTiprel.getTiprelId();
    if (antigo == null) return;
    var remsAntigos = funcionarioRules.getRemuneracoesAssociadosAtivos(antigo.getId());
    var pagsAntigos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(antigo.getId());

    // 1. ENCERRAR por DATA_FIM, mantendo 'A' (doc). Só fecha os que estão em aberto (DATA_FIM nula);
    //    os que já têm DATA_FIM (fim do próprio contrato anterior) ficam como estão. Usa a DATA_FIM
    //    do tiprel anterior (= início do novo contrato, definida no registo).
    var fimAntigo = antigo.getDataFim();
    if (fimAntigo != null) {
      remsAntigos.forEach(r -> { if (r.getDataFim() == null) r.setDataFim(fimAntigo); });
      pagsAntigos.forEach(p -> { if (p.getDataFim() == null) p.setDataFim(fimAntigo); });
    }

    // 2. EXCLUIR da coleção de trabalho do funcionário (scoping em memória). Seguro: @OneToMany
    //    mappedBy (lado inverso) SEM orphanRemoval/REMOVE → não apaga da BD nem mexe no fun_id. Os
    //    def continuam geridos (a DATA_FIM acima persiste) e associados ao tiprel anterior na
    //    RH_T_TIPREL_REM_PAG. Com isto, o sync/reconciliar/associarNovos (que iteram esta coleção)
    //    tratam o novo contrato como TUDO NOVO, sem tocar nos def do contrato anterior.
    var remIds = remsAntigos.stream().map(DefinicaoRemuneracaoEntity::getId).collect(java.util.stream.Collectors.toSet());
    var pagIds = pagsAntigos.stream().map(DefPagamentoEntity::getId).collect(java.util.stream.Collectors.toSet());
    if (funcionario.getDefinicoesRenumeracoes() != null)
      funcionario.getDefinicoesRenumeracoes().removeIf(r -> r != null && r.getId() != null && remIds.contains(r.getId()));
    if (funcionario.getDefinicoesPagamentos() != null)
      funcionario.getDefinicoesPagamentos().removeIf(p -> p != null && p.getId() != null && pagIds.contains(p.getId()));
  }

  /**
   * Revert do registo de Novo Contrato numa validacao NEGATIVA: o novo tiprel deixa de ser o atual
   * e o contrato/tiprel/carreira/mobilidade/regime ANTERIORES (fechados no registo) voltam a ativos.
   * Os registos do NOVO contrato ja foram para 'I' via mudarEstado.
   *
   * <p>So se REATIVA o contrato anterior se ele ainda estiver EM VIGOR (dentro do prazo) — mesma
   * logica do guard D2 (existeContratoEmVigor). Se ja tinha terminado, a rejeicao do novo contrato
   * NAO o ressuscita: o colaborador fica sem relacao ativa (correto, o anterior expirou).
   */
  private void reverterRegistoNovoContrato(TiposRelacionamentoEntity novoTiprel) {
    novoTiprel.setEstActAdm(0);
    var antigo = novoTiprel.getTiprelId();
    if (antigo == null) return;
    var contratoAntigo = antigo.getContrVinculoId();
    if (contratoAntigo == null) return;

    var hoje = LocalDate.now();
    boolean emVigor = contratoAntigo.getDataFim() == null
        || !contratoAntigo.getDataFim().isBefore(hoje);
    if (!emVigor) return;

    // data_fim reposto = data_fim do contrato anterior (o registo tinha-o sobrescrito com a data de
    // inicio do novo); nunca null, para nao perder o termo do contrato.
    var df = contratoAntigo.getDataFim();
    antigo.setEstActAdm(1);
    antigo.setDataFim(df);
    contratoAntigo.setEstado(Estado.A);
    if (antigo.getCarreiraId() != null) antigo.getCarreiraId().setDataFim(df);
    if (antigo.getMobId() != null) antigo.getMobId().setDataFim(df);
    if (antigo.getRegimeId() != null) antigo.getRegimeId().setDataFim(df);
  }

  /**
   * Fecha ou reabre o alerta de conversão de origem conforme a decisão do checker. Localiza-o pelo
   * referencia_id (= id do contrato ANTERIOR, que o alerta referencia) + tipo CONVERSAO_CONTRATO.
   * NO-OP quando não há alerta de conversão associado (Novo Contrato normal).
   *
   * <ul>
   *   <li>SIM (aprovado): estado='I' — contrato convertido, situação resolvida.</li>
   *   <li>NÃO (rejeitado): flg_tratamento='N' — o alerta volta à grelha "por tratar".</li>
   * </ul>
   */
  private void marcarAlertaConversao(ContratoEntity contratoAntigo, boolean aprovado) {
    if (contratoAntigo == null) return;
    alertaEntityRepository
        .findFirstByReferenciaIdAndTipoAlertaOrderByIdDesc(contratoAntigo.getId(), TIPO_ALERTA_CONVERSAO)
        .ifPresent(alerta -> {
          if (aprovado) alerta.setEstado("I");
          else alerta.setFlgTratamento("N");
        });
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
