package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.commands.AtualizarRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.commands.NovaRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoLaboralWriteService {

  @PersistenceContext
  private EntityManager entityManager;

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;
  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;

  @Transactional
  public RelacaoLaboralDTO validar(NovaRelacaoLaboralCommand command) {

    var dto = command.getRelacaolaboral();

    // Caso de uso "Registo de Relação Laboral" (confirmado pelo analista): Mobilidade e Carreira
    // são SOMENTE LEITURA e NÃO vão para validação — a relação laboral só altera a SITUAÇÃO.
    // Neutralizam-se os campos de mobilidade/carreira/salário para os blocos respetivos serem
    // saltados (não atualizam nem criam validações MOBILIDADE/CARREIRA). As alterações de
    // mobilidade/carreira têm os seus ecrãs próprios (.../mobilidades, .../carreiras).
    dto.setTipoMobilidade(null);
    dto.setDirecao(null);
    dto.setSecao(null);
    dto.setLocalTrabalho(null);
    dto.setDataInicioMobilidade(null);
    dto.setDataFimMobilidade(null);
    dto.setTipoAlteracaoCarreira(null);
    dto.setCarreira(null);
    dto.setCategoria(null);
    dto.setEscalao(null);
    dto.setCargo(null);
    dto.setSalario(null);
    dto.setDataInicioCarreira(null);
    dto.setDataFimCarreira(null);

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(atual.getEstado());

    // Caso de uso: só se cria NOVO registo/tiprel quando o registo atual JÁ FOI PROCESSADO
    // (para preservar o histórico salarial). Se ainda não processado -> UPDATE in-place. A
    // condição correta é ultProc (data do último processamento), não flgProcessa (que é 0/1 e
    // está quase sempre preenchido). Alinha com AlterarSituacaoLaboralWriteService.
    // "Processado" detetado como na vista e na edição: EXISTS em RH_T_PROC_FUNCIONARIOS por
    // TIPREL_ID (fonte única de verdade). Processado -> novo registo/tiprel (preserva histórico);
    // não processado -> UPDATE in-place.
    boolean processado = processamentoFuncionarioRepository.existsByTiprel_Id(atual.getId());

    if (!processado) {
      var mob = atual.getMobId();
      if (dto.getTipoMobilidade() != null || dto.getDirecao() != null || dto.getSecao() != null
          || dto.getLocalTrabalho() != null || dto.getDataInicioMobilidade() != null
          || dto.getDataFimMobilidade() != null) {
        if (mob == null) {
          mob = new MobilidadeEntity();
          mob.setEstado(Estado.P);
          mob.setObs("HISTORICO_LABORAL");
          mob.setUuid(IdentificadorUnico.create().valor());
          mob.setFunId(funcionario);
          atual.setMobId(mob);
        }
        populateMobilidade(mob, dto);
        mob.setEstado(Estado.P);
        mobilidadeEntityRepository.save(mob);
        criarValidacaoRelacaoLaboralSeAusente(funcionario, atual, Referencia.MOBILIDADE, mob.getUuid(), mob.getId());

        if (dto.getTipoMobilidade() != null)
          atual.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoMobilidade()));

      }

      var car = atual.getCarreiraId();
      if (dto.getTipoAlteracaoCarreira() != null || dto.getCarreira() != null || dto.getCategoria() != null
          || dto.getEscalao() != null || dto.getSalario() != null || dto.getDataInicioCarreira() != null
          || dto.getDataFimCarreira() != null || dto.getCargo() != null) {
        if (car == null) {
          car = new CarreiraEntity();
          car.setFlgProcessa(1);
          car.setEstado(Estado.P);
          car.setObs("HISTORICO_LABORAL");
          car.setUuid(IdentificadorUnico.create().valor());
          car.setContrVinculoId(atual.getContrVinculoId());
          atual.setCarreiraId(car);
        }
        populateCarreiraCommon(car, dto);
        car.setEstado(Estado.P);
        carreiraEntityRepository.save(car);
        criarValidacaoRelacaoLaboralSeAusente(funcionario, atual, Referencia.CARREIRA, car.getUuid(), car.getId());

        var cargoRef = ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getCargo());
        if (cargoRef != null) atual.setCargoId(cargoRef);
        if (dto.getSalario() != null)
          atual.setSalario(dto.getSalario());
      }

      var sitLab = atual.getSituacLaboralId();
      boolean alterouSituacao = false;
      if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
          || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
        if (sitLab == null) {
          sitLab = new SituacaoLaboralEntity();
          sitLab.setUuid(IdentificadorUnico.create().valor());
          sitLab.setContrVinculoId(atual.getContrVinculoId());
          atual.setSituacLaboralId(sitLab);
        }
        populateSituacao(sitLab, dto);
        // Registo de Relação Laboral aplica-se DIRETAMENTE — o use case NÃO prevê validação
        // para este fluxo (ao contrário de contrato/carreira/substituição). Fica logo ativo.
        sitLab.setEstado(Estado.A);
        situacaoLaboralEntityRepository.save(sitLab);
        // FLG_PROCESSA deriva de RH_T_PARAM_SITUACAO.FLG_REMUNERACAO (use case linha 632-635):
        // ex. Licença S/Vencimento (sem remuneração) -> 0.
        var paramSit = sitLab.getSituacaoLaboralId();
        if (paramSit != null)
          atual.setFlgProcessa(Integer.valueOf(1).equals(paramSit.getFlgRemuneracao()) ? 1 : 0);
        alterouSituacao = true;
      }

      if (dto.getSalario() != null) {
        updateExistingSalaryRemuneracao(funcionario, dto, atual);
      }

      // Sem passo de validação: aplica-se de imediato e gera-se a Ordem de Serviço no apply.
      if (alterouSituacao) {
        funcionarioEntityRepository.saveAndFlush(funcionario);
        ordemServicoWriteService.criar(funcionario, atual, dto.getTipoOrdemServico());
      }

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var hoje = LocalDate.now();
    atual.setEstActAdm(0);
    atual.setDataFim(hoje);

    var novoRelacionamento = dadosContratuaisMapper.clone(atual);
    // Registo de Relação Laboral aplica-se DIRETAMENTE (use case não prevê validação).
    novoRelacionamento.setEstado(Estado.A);
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setTiprelId(atual);
    novoRelacionamento.setReferente("HISTORICO_LABORAL");
    // Use case (RH_T_TIPOS_RELACIONAMENTO): novo registo DATA_INICIO = data do registo, DATA_FIM = nulo.
    // O clone copia as datas do anterior; repõe-se aqui.
    novoRelacionamento.setDataInicio(hoje);
    novoRelacionamento.setDataFim(null);

    List<DefinicaoRemuneracaoEntity> novasRemuneracoes = new ArrayList<>();
    novoRelacionamento.setUltProc(hoje);

    boolean criouAlgum = false;

    if (dto.getTipoMobilidade() != null || dto.getDirecao() != null || dto.getSecao() != null
        || dto.getLocalTrabalho() != null || dto.getDataInicioMobilidade() != null
        || dto.getDataFimMobilidade() != null) {
      var novaMob = new MobilidadeEntity();
      populateMobilidade(novaMob, dto);
      novaMob.setEstado(Estado.P);
      novaMob.setObs("HISTORICO_LABORAL");
      novaMob.setUuid(IdentificadorUnico.create().valor());
      novaMob.setFunId(funcionario);
      funcionario.getMobilidades().add(novaMob);

      novoRelacionamento.setMobId(novaMob);
      novoRelacionamento.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoMobilidade()));
      criouAlgum = true;

      var validMob = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.MOBILIDADE.name(),
          Estado.P);
      validMob.setFunId(funcionario);
      validMob.setTiprelId(novoRelacionamento);
      validMob.setReferenciaUuid(novaMob.getUuid());
      funcionario.getValidacoes().add(validMob);
    }

    if (dto.getTipoAlteracaoCarreira() != null || dto.getCarreira() != null || dto.getCategoria() != null
        || dto.getEscalao() != null || dto.getSalario() != null || dto.getDataInicioCarreira() != null
        || dto.getDataFimCarreira() != null || dto.getCargo() != null) {
      var carAtual = atual.getCarreiraId();
      if (carAtual != null) {
        carAtual.setEstActAdm(0);
        carreiraEntityRepository.save(carAtual);
      }

      var novaCar = new CarreiraEntity();
      populateCarreiraCommon(novaCar, dto);
      novaCar.setFlgProcessa(1);
      novaCar.setEstActAdm(1);
      novaCar.setEstado(Estado.P);
      novaCar.setObs("HISTORICO_LABORAL");
      novaCar.setUuid(IdentificadorUnico.create().valor());
      novaCar.setContrVinculoId(atual.getContrVinculoId());
      carreiraEntityRepository.save(novaCar);

      novoRelacionamento.setCarreiraId(novaCar);
      novoRelacionamento.setSalario(novaCar.getSalario());
      criouAlgum = true;

      var validCar = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CARREIRA.name(),
          Estado.P);
      validCar.setFunId(funcionario);
      validCar.setTiprelId(novoRelacionamento);
      validCar.setReferenciaUuid(novaCar.getUuid());
      funcionario.getValidacoes().add(validCar);

      if (dto.getSalario() != null) {
        var novaRem = closeExistingSalaryAndCreateNew(funcionario, novoRelacionamento, dto);
        novasRemuneracoes.add(novaRem);
      }
    }

    if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
        || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
      var novaSit = new SituacaoLaboralEntity();
      populateSituacao(novaSit, dto);
      novaSit.setEstado(Estado.A);
      novaSit.setUuid(IdentificadorUnico.create().valor());
      novaSit.setContrVinculoId(atual.getContrVinculoId());
      situacaoLaboralEntityRepository.save(novaSit);

      novoRelacionamento.setSituacLaboralId(novaSit);
      // FLG_PROCESSA do novo tiprel deriva de RH_T_PARAM_SITUACAO.FLG_REMUNERACAO (use case 632-635).
      var paramSitNova = novaSit.getSituacaoLaboralId();
      if (paramSitNova != null)
        novoRelacionamento.setFlgProcessa(Integer.valueOf(1).equals(paramSitNova.getFlgRemuneracao()) ? 1 : 0);
      criouAlgum = true;
    }

    funcionario.getTiposrelacionamentos().add(novoRelacionamento);

    funcionarioEntityRepository.saveAndFlush(funcionario);

    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(atual, novoRelacionamento, novasRemuneracoes, Collections.emptyList());

    // Sem passo de validação (use case): o novo tipo de relacionamento fica ativo de imediato.
    // Gera-se a Ordem de Serviço no apply (opcional — só se o tipo vier preenchido pelo frontend).
    if (criouAlgum) {
      ordemServicoWriteService.criar(funcionario, novoRelacionamento, dto.getTipoOrdemServico());
    }

    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  @Transactional
  public RelacaoLaboralDTO atualizar(AtualizarRelacaoLaboralCommand command) {
    var dto = command.getRelacaolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    // A relação laboral é identificada pelo UUID do TIPO DE RELACIONAMENTO (tiprel),
    // exposto na lista como `tiprelUuid` — e não pelo UUID da carreira (a carreira é
    // somente leitura). O path {carreiraId} transporta agora o tiprelUuid.
    var relacionamento = tiposRelacionamentoEntityRepository
        .findByUuid(UUID.fromString(command.getTiprelUuid()))
        .orElse(null);
    if (relacionamento == null) {
      throw IgrpResponseStatusException.notFound("Histórico Laboral não encontrado");
    }

    if (relacionamento.getFunId() == null || !relacionamento.getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Histórico laboral não pertence ao funcionário");

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(relacionamento.getEstado());

    // A edição da relação laboral só é permitida se ainda NÃO houver processamento salarial.
    // Deteção feita como na vista (RH_V_CONTRATO/RH_V_RELACAO_LABORAL): EXISTS em
    // RH_T_PROC_FUNCIONARIOS por TIPREL_ID. Se já processado, bloquear — alterar exigiria
    // novo registo/validação, não uma simples edição in-place.
    if (processamentoFuncionarioRepository.existsByTiprel_Id(relacionamento.getId()))
      throw IgrpResponseStatusException.badRequest(
          "Não é possível editar a relação laboral: já tem processamento salarial.");

    // Mobilidade e Carreira são SOMENTE LEITURA na relação laboral (só se altera a situação).
    // Neutralizar os campos para os blocos respetivos serem saltados.
    dto.setTipoMobilidade(null);
    dto.setDirecao(null);
    dto.setSecao(null);
    dto.setLocalTrabalho(null);
    dto.setDataInicioMobilidade(null);
    dto.setDataFimMobilidade(null);
    dto.setTipoAlteracaoCarreira(null);
    dto.setCarreira(null);
    dto.setCategoria(null);
    dto.setEscalao(null);
    dto.setCargo(null);
    dto.setSalario(null);
    dto.setDataInicioCarreira(null);
    dto.setDataFimCarreira(null);

    if (dto.getTipoMobilidade() != null || dto.getDirecao() != null || dto.getSecao() != null
        || dto.getLocalTrabalho() != null || dto.getDataInicioMobilidade() != null
        || dto.getDataFimMobilidade() != null) {
      var mob = relacionamento.getMobId();
      if (mob == null) {
        mob = new MobilidadeEntity();
        mob.setEstado(Estado.P);
        mob.setObs("HISTORICO_LABORAL");
        mob.setUuid(IdentificadorUnico.create().valor());
        mob.setFunId(funcionario);

      }
      populateMobilidade(mob, dto);
      mobilidadeEntityRepository.save(mob);

      relacionamento.setMobId(mob);
      if (dto.getTipoMobilidade() != null)
        relacionamento.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoMobilidade()));

    }

    if (dto.getTipoAlteracaoCarreira() != null || dto.getCarreira() != null || dto.getCategoria() != null
        || dto.getEscalao() != null || dto.getSalario() != null || dto.getDataInicioCarreira() != null
        || dto.getDataFimCarreira() != null || dto.getCargo() != null) {
      var car = relacionamento.getCarreiraId();
      if (car == null) {
        car = new CarreiraEntity();
        car.setFlgProcessa(1);
        car.setEstado(Estado.P);
        car.setObs("HISTORICO_LABORAL");
        car.setUuid(IdentificadorUnico.create().valor());
        car.setContrVinculoId(relacionamento.getContrVinculoId());
      }
      populateCarreiraCommon(car, dto);
      carreiraEntityRepository.save(car);

      relacionamento.setCarreiraId(car);
      var cargoRef2 = ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getCargo());
      if (cargoRef2 != null) relacionamento.setCargoId(cargoRef2);
      if (dto.getSalario() != null)
        relacionamento.setSalario(dto.getSalario());
    }

    if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
        || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
      var sitLab = relacionamento.getSituacLaboralId();
      if (sitLab == null) {
        sitLab = new SituacaoLaboralEntity();
        sitLab.setEstado(Estado.A);
        sitLab.setUuid(IdentificadorUnico.create().valor());
        sitLab.setContrVinculoId(relacionamento.getContrVinculoId());
      }
      // Atualização in-place: apenas se atualizam os campos da situação. NÃO se altera o ESTADO
      // do registo — a relação laboral não passa por validação (use case). Sem OS neste fluxo.
      populateSituacao(sitLab, dto);
      situacaoLaboralEntityRepository.save(sitLab);
      relacionamento.setSituacLaboralId(sitLab);
    }

    if (dto.getSalario() != null) {
      updateExistingSalaryRemuneracao(funcionario, dto,relacionamento);
    }

    tiposRelacionamentoEntityRepository.save(relacionamento);
    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  /**
   * Regra geral (caso de uso linha 5): toda alteração vai para validação. Cria uma validação
   * pendente INSERT para a sub-entidade alterada no fluxo relação laboral (update in-place),
   * sem duplicar se já houver uma pendente para a mesma referência.
   */
  private void criarValidacaoRelacaoLaboralSeAusente(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprel,
      Referencia referencia, java.util.UUID referenciaUuid, Long referenciaId) {
    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, referencia)) return;
    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), referencia.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiprel);
    valid.setReferenciaUuid(referenciaUuid);
    valid.setReferenciaId(referenciaId);
    funcionario.getValidacoes().add(valid);
  }

  private void populateMobilidade(MobilidadeEntity mob, RelacaoLaboralDTO dto) {
    if (dto.getTipoMobilidade() != null)
      mob.setTipoSituacao(dto.getTipoMobilidade());
    var instRef = ValidationUtil.ref(entityManager, DirecaoEntity.class, dto.getDirecao());
    if (instRef != null) mob.setInstidId(instRef);
    var secRef = ValidationUtil.ref(entityManager, SecaoEntity.class, dto.getSecao());
    if (secRef != null) mob.setSecaoId(secRef);
    var ltRef = ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, dto.getLocalTrabalho());
    if (ltRef != null) mob.setLocalTrabId(ltRef);
    if (dto.getDataInicioMobilidade() != null)
      mob.setDataInicio(dto.getDataInicioMobilidade());
    if (dto.getDataFimMobilidade() != null)
      mob.setDataFim(dto.getDataFimMobilidade());
  }

  private void populateCarreiraCommon(CarreiraEntity car, RelacaoLaboralDTO dto) {
    var cargoRef = ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getCargo());
    if (cargoRef != null) car.setCargoId(cargoRef);
    var escalaoRef = ValidationUtil.ref(entityManager, ParamEscalaoEntity.class, dto.getEscalao());
    if (escalaoRef != null) car.setEscalaoId(escalaoRef);
    var catRef = ValidationUtil.ref(entityManager, ParamCategoriaEntity.class, dto.getCategoria());
    if (catRef != null) car.setCategoriaId(catRef);
    var carrRef = ValidationUtil.ref(entityManager, ParamCarreiraEntity.class, dto.getCarreira());
    if (carrRef != null) car.setCarrPccsId(carrRef);
    if (dto.getSalario() != null)
      car.setSalario(dto.getSalario());
    else if (car.getSalario() == null)
      car.setSalario(BigDecimal.ZERO);
    if (dto.getTipoAlteracaoCarreira() != null)
      car.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoAlteracaoCarreira()));
    if (dto.getDataInicioCarreira() != null)
      car.setDataInicio(dto.getDataInicioCarreira());
    if (dto.getDataFimCarreira() != null)
      car.setDataFim(dto.getDataFimCarreira());
  }

  private void populateSituacao(SituacaoLaboralEntity sit, RelacaoLaboralDTO dto) {
    var sitRef = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class, dto.getSituacaoLaboral());
    if (sitRef != null) sit.setSituacaoLaboralId(sitRef);
    if (dto.getMotivo() != null) {
      try {
        var mid = Long.parseLong(dto.getMotivo());
        var motRef = ValidationUtil.ref(entityManager, ParamSituacaoDetalheEntity.class, mid);
        if (motRef != null) sit.setMotivoSitLabId(motRef);
      } catch (NumberFormatException ignored) {
      }
      sit.setTipoSituacao(ValidationUtil.trimToNull(dto.getMotivo()));
    }
    if (dto.getDataInicioSituacao() != null)
      sit.setDataInicio(dto.getDataInicioSituacao());
    if (dto.getDataFimSituacao() != null)
      sit.setDataFim(dto.getDataFimSituacao());
    if (dto.getObservacao() != null)
      sit.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
  }

  private void updateExistingSalaryRemuneracao(FuncionarioEntity funcionario, RelacaoLaboralDTO dto,
                                               TiposRelacionamentoEntity tiposRelacionamento) {

    //atraves de vinculo sabemos o tm_id sall, associação feita na tabela paramVinculoMovimento
    var vinculoId = tiposRelacionamento.getContrVinculoId().getVinculoId().getId();
    var paramVinculoMovimentoEntity =
        paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(vinculoId,"REM").getFirst();

    var tmSalario = paramVinculoMovimentoEntity.getTmId();

    var renumeracoes = funcionario.getDefinicoesRenumeracoes();
    for (var rem : renumeracoes) {
      if (rem.getTmId() != null && rem.getTmId().getId().equals(tmSalario.getId()) && rem.getEstado() == Estado.P) {
        rem.setValor(dto.getSalario());
        if (dto.getDataInicioCarreira() != null)
          rem.setDataInicio(dto.getDataInicioCarreira());
        if (dto.getDataFimCarreira() != null)
          rem.setDataFim(dto.getDataFimCarreira());
        definicaoRemuneracaoEntityRepository.save(rem);
      }
    }
  }

  private DefinicaoRemuneracaoEntity closeExistingSalaryAndCreateNew(FuncionarioEntity funcionario, TiposRelacionamentoEntity rel,
                                               RelacaoLaboralDTO dto) {

    var vinculoId = rel.getContrVinculoId().getVinculoId().getId();
    var paramVinculoMovimentoEntity =
        paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(vinculoId,"REM").getFirst();

    var tmSalario = paramVinculoMovimentoEntity.getTmId();

    var anteriores = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    for (var ant : anteriores) {
      if (ant.getTmId() != null && ant.getTmId().getId().equals(tmSalario.getId())) {
        ant.setDataFim(dto.getDataInicioCarreira() != null ? dto.getDataInicioCarreira().minusDays(1)
            : LocalDate.now().minusDays(1));
        definicaoRemuneracaoEntityRepository.save(ant);
      }
    }
    var renumSal = definicaoRemuneracaoMapper.createRenumeracao(dto.getSalario(), tmSalario,
        dto.getDataInicioCarreira(), dto.getDataFimCarreira(), funcionario, null);
    definicaoRemuneracaoEntityRepository.save(renumSal);
    var remun = new RemuneracaoTiprelEntity();
    remun.setEstado(Estado.P);
    remun.setUuid(IdentificadorUnico.create().valor());
    remun.setRemId(renumSal);
    remun.setTiprelId(rel);
    remuneracaoTiprelEntityRepository.save(remun);
    return renumSal;
  }

  private void updateEstadoOnRelAndChildren(TiposRelacionamentoEntity rel, Estado estado) {
    rel.setEstado(estado);
    var contrato = rel.getContrVinculoId();
    if (contrato != null)
      contrato.setEstado(estado);
    if (rel.getMobId() != null)
      rel.getMobId().setEstado(estado);
    if (rel.getCarreiraId() != null)
      rel.getCarreiraId().setEstado(estado);
    if (rel.getSituacLaboralId() != null)
      rel.getSituacLaboralId().setEstado(estado);
  }

  private void updateValidacaoPendentes(UUID uuid, Estado estado) {
    funcionarioRules.getValidacaoPendente(uuid, TipoAcao.INSERT, Referencia.MOBILIDADE)
        .ifPresent(v -> v.setEstado(estado));
    funcionarioRules.getValidacaoPendente(uuid, TipoAcao.INSERT, Referencia.CARREIRA)
        .ifPresent(v -> v.setEstado(estado));
    funcionarioRules.getValidacaoPendente(uuid, TipoAcao.INSERT, Referencia.SITUACAO_LABORAL)
        .ifPresent(v -> v.setEstado(estado));
  }

}
