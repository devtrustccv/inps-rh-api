package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.AlterarSituacaoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.AlterarSituacaoLaboralRequest;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.AusenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AusenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SituacaoLaboralEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Alterar Situação Laboral (Inativar/Ativar Colaborador + Licença S/Vencimento e outras situações).
 * O {@link #execute} é apenas o dispatcher: cada caminho de negócio (CORRIGIR, validar, reenvio de
 * correção, registo processado/não-processado) vive no seu próprio método privado, e a lógica
 * partilhada (flg_processa, ausência, carimbo JaVers, cópia de rem/pag) está em helpers reutilizados.
 */
@Service
@RequiredArgsConstructor
public class AlterarSituacaoLaboralWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlterarSituacaoLaboralWriteService.class);

  private static final String TABELA_SITUACAO = "RH_T_SITUACAO_LABORAL";
  private static final String REFERENTE_SITUACAO = "SITUACAO_LABORAL";

  // Domínio ESTADO_CONTRATO (RH_T_PARAM_SITUACAO.FLG_ESTADO_CONTRATO): A=Ativo, C=Cessado, S=Suspenso.
  // A spec DOSSIÊ (01/09) tornou esta flag a fonte de verdade do estado do colaborador — a decisão
  // deixa de estar presa ao CODIGO da situação (ex.: APOSENTADO tem flag C e também cessa).
  private static final String ESTADO_CONTRATO_ATIVO = "A";
  private static final String ESTADO_CONTRATO_CESSADO = "C";

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final AusenciaEntityRepository ausenciaEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;

  @Transactional
  public SuccessResponseDTO execute(AlterarSituacaoLaboralCommand command) {

    var dto = command.getAlterarsituacaolaboral();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(command.getId()).valor());

    // CORRIGIR (checker devolve ao maker): situação/validação P -> C, SEM aplicar payload.
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      return devolverParaCorrecao(funcionario);
    }

    var param = paramSitLaboralEntityRepository.getReferenceById(dto.getSituacaoLaboralId());
    var motivo = dto.getMotivoId() != null
        ? paramSituacaoDetalheEntityRepository.getReferenceById(dto.getMotivoId()) : null;

    boolean estaPorCorrigir = funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.ESTADO_COLABORADOR);
    if (estaPorCorrigir && dto.getValidar() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Situação laboral em correção: não pode ser validada. Corrija e reenvie primeiro.");
    }
    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.ESTADO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "O funcionário '%s' não possui uma validação pendente de alteração de situação laboral."
              .formatted(funcionario.getNome()));
    }

    // CHECKER: aprovar (SIM) ou rejeitar (NAO).
    if (dto.getValidar() != null) {
      return validar(dto, funcionario, param, motivo);
    }

    // MAKER: guards do registo/reenvio.
    if (dto.getMotivoId() == null) {
      throw IgrpResponseStatusException.badRequest("O motivo da alteração da situação laboral é obrigatório.");
    }
    guardComboInativarAtivar(estaPorCorrigir, param, funcionario);
    guardDataFimObrigatoriaEmAusencia(param, dto);

    var dataInicio = DateFormatter.stringToLocalDate(dto.getDataInicio());
    var dataFim = DateFormatter.stringToLocalDate(dto.getDataFim());
    var tiprelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    funcionarioRules.garantirEditavel(tiprelAtual.getEstado());

    // Maker reenvia a correção (C -> P): edita in place o registo devolvido e reabre para validação.
    if (estaPorCorrigir) {
      return reenviarCorrecao(dto, funcionario, param, motivo, dataInicio, dataFim, tiprelAtual);
    }

    var situacaoAtual = tiprelAtual.getSituacLaboralId();
    if (!mudouSituacaoOuMotivo(situacaoAtual, dto)) {
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral sem alterações.", List.of());
    }

    // Só há novo registo quando o tiprel atual já foi processado; senão, UPDATE in place.
    return tiprelAtual.getUltProc() != null
        ? registarProcessado(dto, funcionario, param, motivo, dataInicio, dataFim, tiprelAtual)
        : registarNaoProcessado(dto, funcionario, param, motivo, dataInicio, dataFim, tiprelAtual, situacaoAtual);
  }

  // ------------------------------------------------------------------------------------------------
  // Caminhos de negócio
  // ------------------------------------------------------------------------------------------------

  /** Checker devolve ao maker: situação pendente P -> C e validação P -> C. */
  private SuccessResponseDTO devolverParaCorrecao(FuncionarioEntity funcionario) {
    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var situacao = tiprel.getSituacLaboralId();
    if (situacao == null || situacao.getEstado() != Estado.P
        || !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.ESTADO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "Não há alteração de situação laboral pendente para devolver para correção.");
    }
    funcionarioRules.devolverParaCorrecao(situacao.getUuid(), Estado.P, Referencia.ESTADO_COLABORADOR);
    situacao.setEstado(Estado.C);
    tiprel.setEstado(Estado.C);
    funcionario.setEstadoValidacao(Estado.C.name());
    funcionarioEntityRepository.saveAndFlush(funcionario);
    LOGGER.info("[CORRIGIR] ESTADO_COLABORADOR devolvido para correção (situacao={}).", situacao.getUuid());
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
        "Situação laboral devolvida para correção.", List.of());
  }

  /** Checker aprova (SIM -> A) ou rejeita (NAO -> I) a alteração pendente. */
  private SuccessResponseDTO validar(AlterarSituacaoLaboralRequest dto, FuncionarioEntity funcionario,
      ParamSituacaoEntity param, ParamSituacaoDetalheEntity motivo) {
    var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    var tiprelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // Só o ramo "processado" cria um NOVO tiprel pendente (estado=P) que fechou o anterior; no ramo
    // "não processado" o tiprel fica estado=A e nada foi fechado — logo a rejeição não reabre nada.
    boolean tiprelEraPendente = tiprelAtual.getEstado() == Estado.P;
    // Rejeitar a ALTERACAO nao invalida o VINCULO: no ramo "nao processado" o tiprel corrente estava A e
    // nada foi fechado, logo tem de continuar A (e o que o comentario acima sempre declarou). Carimba-lo
    // I deixava o vinculo corrente de um colaborador activo marcado como historico e, com o guard
    // garantirEditavel activo, bloqueava todo o dossie por causa de um simples NAO do checker.
    tiprelAtual.setEstado(estado == Estado.I && !tiprelEraPendente ? Estado.A : estado);

    var situacao = tiprelAtual.getSituacLaboralId();
    situacao.setEstado(estado);
    situacao.setMotivoSitLabId(motivo);
    situacao.setSituacaoLaboralId(param);
    situacao.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    situacaoLaboralEntityRepository.save(situacao);

    funcionario.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> Referencia.ESTADO_COLABORADOR.name().equals(v.getReferenciaName())
            && TipoAcao.UPDATE.name().equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));

    // O ciclo de validação fechou (aprovado OU rejeitado): o registo do colaborador volta a estar
    // validado. Rejeitar a ALTERAÇÃO não invalida o REGISTO — por isso 'A' nos dois ramos, e nunca 'I'.
    funcionario.setEstadoValidacao(Estado.A.name());

    if (estado == Estado.A) {
      if (cessaContrato(param)) {
        aplicarEfeitosCessacao(dto, funcionario, tiprelAtual);
      } else if (ativaContrato(param)) {
        aplicarEfeitosReativacao(funcionario, tiprelAtual);
      }
      ordemServicoWriteService.criar(funcionario, tiprelAtual, dto.getTipoOrdemServico());
    } else {
      rollbackRejeicao(funcionario, tiprelAtual, tiprelEraPendente);
    }

    funcionarioEntityRepository.save(funcionario);
    var mensagem = EstadoValidacao.SIM.equals(dto.getValidar()) ? "Situação laboral validada." : "Situação laboral rejeitada.";
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
  }

  /** Maker reenvia a correção devolvida (C -> P), editando o mesmo registo, com carimbo JaVers. */
  private SuccessResponseDTO reenviarCorrecao(AlterarSituacaoLaboralRequest dto, FuncionarioEntity funcionario,
      ParamSituacaoEntity param, ParamSituacaoDetalheEntity motivo, LocalDate dataInicio, LocalDate dataFim,
      TiposRelacionamentoEntity tiprelAtual) {
    var situacao = tiprelAtual.getSituacLaboralId();
    if (situacao == null) {
      throw IgrpResponseStatusException.badRequest("Situação laboral em correção não encontrada.");
    }
    situacao.setSituacaoLaboralId(param);
    situacao.setMotivoSitLabId(motivo);
    situacao.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    situacao.setDataInicio(dataInicio);
    situacao.setDataFim(dataFim);
    situacao.setEstado(Estado.P);
    tiprelAtual.setEstado(Estado.P);
    tiprelAtual.setFlgProcessa(flgProcessaDe(param));
    funcionario.setEstadoValidacao(Estado.P.name());
    var validacao = funcionarioRules.reabrirParaValidacao(situacao.getUuid(), Referencia.ESTADO_COLABORADOR);
    salvarSituacaoComAudit(validacao, situacao);
    funcionarioEntityRepository.saveAndFlush(funcionario);
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
        "Situação laboral corrigida e reenviada para validação.", List.of());
  }

  /** Registo quando o tiprel atual ainda NÃO foi processado: UPDATE in place da situação existente. */
  private SuccessResponseDTO registarNaoProcessado(AlterarSituacaoLaboralRequest dto, FuncionarioEntity funcionario,
      ParamSituacaoEntity param, ParamSituacaoDetalheEntity motivo, LocalDate dataInicio, LocalDate dataFim,
      TiposRelacionamentoEntity tiprelAtual, SituacaoLaboralEntity situacaoAtual) {
    tiprelAtual.setFlgProcessa(flgProcessaDe(param));
    var validacao = garantirValidacaoPendente(funcionario, tiprelAtual, situacaoAtual);

    if (situacaoAtual != null) {
      situacaoAtual.setSituacaoLaboralId(param);
      situacaoAtual.setMotivoSitLabId(motivo);
      situacaoAtual.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
      situacaoAtual.setDataInicio(dataInicio);
      situacaoAtual.setDataFim(dataFim);
      situacaoAtual.setEstado(Estado.P);
      salvarSituacaoComAudit(validacao, situacaoAtual);
      criarAusenciaSeAplicavel(funcionario, param, situacaoAtual, dataInicio, dataFim);
    }
    // Enviado para validação: o "Estado do Registo" da grelha passa a Pendente. NÃO se toca em
    // funcionario.estado — o colaborador continua ativo enquanto a alteração espera aprovação.
    funcionario.setEstadoValidacao(Estado.P.name());
    funcionarioEntityRepository.save(funcionario);
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral actualizada.", List.of());
  }

  /** Registo quando o tiprel atual JÁ foi processado: fecha o atual e cria novo (situação + tiprel). */
  private SuccessResponseDTO registarProcessado(AlterarSituacaoLaboralRequest dto, FuncionarioEntity funcionario,
      ParamSituacaoEntity param, ParamSituacaoDetalheEntity motivo, LocalDate dataInicio, LocalDate dataFim,
      TiposRelacionamentoEntity tiprelAtual) {
    // Spec DOSSIÊ 1.1 + regra do analista: anterior DATA_FIM = data início (do formulário) - 1, para o
    // relacionamento fechado terminar em inicio-1, contíguo com o novo (que abre em inicio) sem sobrepor.
    tiprelAtual.setDataFim(dataInicio.minusDays(1));
    tiprelAtual.setEstActAdm(0);

    var situacao = new SituacaoLaboralEntity();
    situacao.setUuid(UuidCreator.getTimeOrdered());
    situacao.setSituacaoLaboralId(param);
    situacao.setMotivoSitLabId(motivo);
    situacao.setContrVinculoId(tiprelAtual.getContrVinculoId());
    situacao.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    situacao.setDataInicio(dataInicio);
    situacao.setDataFim(dataFim);
    situacao.setEstado(Estado.P);
    situacaoLaboralEntityRepository.save(situacao);

    var novoTiprel = dadosContratuaisMapper.clone(tiprelAtual);
    // Spec DOSSIÊ 1.2: novo tiprel DATA_INICIO = data início (do formulário), DATA_FIM = null.
    novoTiprel.setDataInicio(dataInicio);
    novoTiprel.setDataFim(null);
    novoTiprel.setEstActAdm(1);
    novoTiprel.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
    novoTiprel.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
    novoTiprel.setSituacLaboralId(situacao);
    novoTiprel.setReferente(REFERENTE_SITUACAO);
    novoTiprel.setEstado(Estado.P);
    novoTiprel.setFlgProcessa(flgProcessaDe(param));
    var tiprelPersistido = tiposRelacionamentoEntityRepository.saveAndFlush(novoTiprel);

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tiprelPersistido);
    validacao.setReferenciaUuid(situacao.getUuid());
    funcionario.getValidacoes().add(validacao);

    funcionario.setEstado(estadoDoFuncionarioPara(param, funcionario));
    funcionario.setEstadoValidacao(Estado.P.name());
    funcionarioEntityRepository.save(funcionario);

    copiarRemPag(tiprelAtual, tiprelPersistido);
    criarAusenciaSeAplicavel(funcionario, param, situacao, dataInicio, dataFim);

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Situação laboral actualizada.", List.of());
  }

  // ------------------------------------------------------------------------------------------------
  // Efeitos e helpers partilhados
  // ------------------------------------------------------------------------------------------------

  /** Cessação APROVADA → colaborador inativo (ESTADO=I) e fecho da cadeia (datas fim). */
  private void aplicarEfeitosCessacao(AlterarSituacaoLaboralRequest dto, FuncionarioEntity funcionario,
      TiposRelacionamentoEntity tiprelAtual) {
    funcionario.setEstado(Estado.I);
    var dataFim = DateFormatter.stringToLocalDate(dto.getDataFim());
    tiprelAtual.setDataFim(dataFim);
    // NÃO zerar est_act_adm: o vínculo cessado continua a ser o "último/corrente" do colaborador
    // (só passa a inativo). Assim o inativo mantém-se visível na lista (RH_V_DOSSIE.ULTIMO_VINCULO=1),
    // como exige a spec (colaborador inativo visível, só com ação Ativar/Inativar). Alinhado com o
    // AlterarEstadoContratoService, que na desativação também preserva est_act_adm=1.

    var mobilidade = tiprelAtual.getMobId();
    if (mobilidade != null) mobilidade.setDataFim(dataFim);
    var carreira = tiprelAtual.getCarreiraId();
    if (carreira != null) carreira.setDataFim(dataFim);
    var contrato = tiprelAtual.getContrVinculoId();
    if (contrato != null) contrato.setDataFim(dataFim);

    funcionario.getDefinicoesRenumeracoes().forEach(r -> r.setDataFim(dataFim));
    funcionario.getDefinicoesPagamentos().forEach(p -> p.setDataFim(dataFim));
  }

  /**
   * Reativação APROVADA → colaborador ativo (ESTADO=A) e reabertura da cadeia (datas fim a null).
   * Simétrico de {@link #aplicarEfeitosCessacao}: sem isto, aprovar um "Ativar" marcava a situação e a
   * validação como A mas deixava o colaborador em I com o vínculo fechado — metade do ecrã
   * Ativar/Inativar não funcionava.
   *
   * <p>Tal como na cessação, NÃO se mexe em est_act_adm: o vínculo continua a ser o corrente.
   *
   * <p>Limitação conhecida: a cessação sobrescreve {@code contrato.DATA_FIM} com a data de cessação, pelo
   * que o termo original de um contrato a prazo já se perdeu nesse momento e não é recuperável aqui.
   * Reabrir (null) é o menos errado — deixar a data da cessação descreveria um contrato terminado por um
   * evento que foi revertido. Ver o achado da edição in-place no handoff.
   */
  private void aplicarEfeitosReativacao(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprelAtual) {
    funcionario.setEstado(Estado.A);
    tiprelAtual.setDataFim(null);

    var mobilidade = tiprelAtual.getMobId();
    if (mobilidade != null) mobilidade.setDataFim(null);
    var carreira = tiprelAtual.getCarreiraId();
    if (carreira != null) carreira.setDataFim(null);
    var contrato = tiprelAtual.getContrVinculoId();
    if (contrato != null) contrato.setDataFim(null);

    funcionario.getDefinicoesRenumeracoes().forEach(r -> r.setDataFim(null));
    funcionario.getDefinicoesPagamentos().forEach(p -> p.setDataFim(null));
  }

  /**
   * Rejeição (validar=NAO): rollback ao estado pré-registo. O registo (maker) fechou o tiprel anterior
   * e criou este novo pendente; ao rejeitar, reabre o anterior, descarta o novo e repõe o ESTADO do
   * colaborador conforme a situação que estava vigente. Só se aplica quando este tiprel era pendente.
   */
  private void rollbackRejeicao(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprelAtual,
      boolean tiprelEraPendente) {
    var anterior = tiprelAtual.getTiprelId();
    boolean fechadoPeloRegisto = tiprelEraPendente && anterior != null
        && Integer.valueOf(0).equals(anterior.getEstActAdm());
    if (!fechadoPeloRegisto) return;

    anterior.setEstActAdm(1);
    anterior.setDataFim(null);
    tiposRelacionamentoEntityRepository.save(anterior);
    tiprelAtual.setEstActAdm(0);

    var situacaoAnterior = anterior.getSituacLaboralId();
    var paramAnterior = (situacaoAnterior != null) ? situacaoAnterior.getSituacaoLaboralId() : null;
    funcionario.setEstado(cessaContrato(paramAnterior) ? Estado.I : Estado.A);
  }

  /** Get-or-create de uma validação pendente UPDATE/ESTADO_COLABORADOR, persistida (id/uuid) para o audit. */
  private ValidacaoEntity garantirValidacaoPendente(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprel,
      SituacaoLaboralEntity situacao) {
    var existente = funcionarioRules
        .getValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.ESTADO_COLABORADOR)
        .orElse(null);
    if (existente != null) return existente;

    var nova = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    nova.setFunId(funcionario);
    nova.setTiprelId(tiprel);
    nova.setReferenciaUuid(situacao != null ? situacao.getUuid() : null);
    funcionario.getValidacoes().add(nova);
    funcionarioEntityRepository.saveAndFlush(funcionario); // persiste a validação -> id/uuid
    return nova;
  }

  /**
   * Auto-audit (JaVers): carimba o save da situação com a validação em curso — o "detalhe de alterações"
   * filtra por este validacaoUuid (situacaoLaboralId/motivo/datas/obs).
   */
  private void salvarSituacaoComAudit(ValidacaoEntity validacao, SituacaoLaboralEntity situacao) {
    try {
      ValidacaoAuditContext.set(validacao.getId(), validacao.getUuid(), TABELA_SITUACAO);
      situacaoLaboralEntityRepository.save(situacao);
    } finally {
      ValidacaoAuditContext.clear();
    }
  }

  /** Cria RH_T_AUSENCIA quando a situação tem flg_ausencia=1 (ex.: Licença S/Vencimento). Idempotente. */
  private void criarAusenciaSeAplicavel(FuncionarioEntity funcionario, ParamSituacaoEntity param,
      SituacaoLaboralEntity situacao, LocalDate dataInicio, LocalDate dataFim) {
    if (!Integer.valueOf(1).equals(param.getFlgAusencia())) return;
    if (situacao.getId() != null
        && !ausenciaEntityRepository.findAllByReferenciaNameAndReferenciaId(REFERENTE_SITUACAO, situacao.getId()).isEmpty()) {
      return; // já existe ausência para esta situação (reenvio/re-registo)
    }
    var ausencia = new AusenciaEntity();
    ausencia.setUuid(UuidCreator.getTimeOrdered());
    ausencia.setFunId(funcionario);
    ausencia.setParamSitId(param);
    ausencia.setDataInicio(dataInicio);
    ausencia.setDataFim(dataFim);
    ausencia.setReferenciaName(REFERENTE_SITUACAO);
    ausencia.setReferenciaId(situacao.getId());
    ausencia.setEstado(Estado.P);
    ausenciaEntityRepository.save(ausencia);
  }

  /** Copia as entradas RH_T_TIPREL_REM_PAG do tiprel de origem para o de destino (mesmos rem/pag). */
  private void copiarRemPag(TiposRelacionamentoEntity origem, TiposRelacionamentoEntity destino) {
    var novas = tipoRelRemPagEntityRepository.findByTiprelId_Id(origem.getId()).stream().map(e -> {
      var nova = new TipoRelRemPagEntity();
      nova.setTiprelId(destino);
      nova.setRemId(e.getRemId());
      nova.setPagId(e.getPagId());
      return nova;
    }).toList();
    tipoRelRemPagEntityRepository.saveAll(novas);
  }

  private int flgProcessaDe(ParamSituacaoEntity param) {
    return Integer.valueOf(1).equals(param.getFlgRemuneracao()) ? 1 : 0;
  }

  private Estado estadoDoFuncionarioPara(ParamSituacaoEntity param, FuncionarioEntity funcionario) {
    var flg = param != null ? param.getFlgEstadoContrato() : null;
    if (ESTADO_CONTRATO_CESSADO.equalsIgnoreCase(flg)) return Estado.I;
    if (ESTADO_CONTRATO_ATIVO.equalsIgnoreCase(flg)) return Estado.A;
    // Suspenso (S) e situações de ausência (ex.: Licença) não alteram o estado do colaborador.
    return funcionario.getEstado();
  }

  /** Situação cujo estado de contrato (FLG_ESTADO_CONTRATO) é 'C' (Cessado) — cessa o vínculo. */
  private boolean cessaContrato(ParamSituacaoEntity param) {
    return param != null && ESTADO_CONTRATO_CESSADO.equalsIgnoreCase(param.getFlgEstadoContrato());
  }

  /** Situação cujo estado de contrato (FLG_ESTADO_CONTRATO) é 'A' (Ativo) — (re)ativa o vínculo. */
  private boolean ativaContrato(ParamSituacaoEntity param) {
    return param != null && ESTADO_CONTRATO_ATIVO.equalsIgnoreCase(param.getFlgEstadoContrato());
  }

  private boolean mudouSituacaoOuMotivo(SituacaoLaboralEntity situacaoAtual, AlterarSituacaoLaboralRequest dto) {
    Long sitAtualId = (situacaoAtual != null && situacaoAtual.getSituacaoLaboralId() != null)
        ? situacaoAtual.getSituacaoLaboralId().getId() : null;
    Long motAtualId = (situacaoAtual != null && situacaoAtual.getMotivoSitLabId() != null)
        ? situacaoAtual.getMotivoSitLabId().getId() : null;
    return !Objects.equals(sitAtualId, dto.getSituacaoLaboralId()) || !Objects.equals(motAtualId, dto.getMotivoId());
  }

  // ------------------------------------------------------------------------------------------------
  // Guards do maker
  // ------------------------------------------------------------------------------------------------

  /** Combo Inativar/Ativar (2 opções): não cessar quem já está inativo, nem ativar quem já está ativo. */
  private void guardComboInativarAtivar(boolean estaPorCorrigir, ParamSituacaoEntity param, FuncionarioEntity funcionario) {
    if (estaPorCorrigir) return; // o reenvio de correção repõe o mesmo estado
    if (cessaContrato(param) && funcionario.getEstado() == Estado.I) {
      throw IgrpResponseStatusException.badRequest("O colaborador já está inativo; não é necessária esta ação.");
    }
    if (ativaContrato(param) && funcionario.getEstado() == Estado.A) {
      throw IgrpResponseStatusException.badRequest("O colaborador já está ativo; não é necessária esta ação.");
    }
  }

  /** Situações de ausência (flg_ausencia=1, ex.: Licença S/Vencimento) exigem data fim (período). */
  private void guardDataFimObrigatoriaEmAusencia(ParamSituacaoEntity param, AlterarSituacaoLaboralRequest dto) {
    if (Integer.valueOf(1).equals(param.getFlgAusencia()) && ValidationUtil.trimToNull(dto.getDataFim()) == null) {
      throw IgrpResponseStatusException.badRequest(
          "A data fim é obrigatória para situações de ausência (ex.: Licença Sem Vencimento).");
    }
  }
}
