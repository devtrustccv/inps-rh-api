package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.commands.AtualizarRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.commands.NovaRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
  private final TipoMovimentoHelper tipoMovimentoHelper;

  @Transactional
  public ValidarNovoHistoricoLaboralDTO validar(NovaRelacaoLaboralCommand command) {

    var dto = command.getValidarnovohistoricolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var flgProc = atual.getFlgProcessa();

    if (flgProc != null) {
      var mob = atual.getMobId();
      if (dto.getTipoMobilidade() != null || dto.getDirecao() != null || dto.getSecao() != null
          || dto.getLocalTrabalho() != null || dto.getDataInicioMobilidade() != null
          || dto.getDataFimMobilidade() != null) {
        populateMobilidade(mob, dto);
        mob.setEstado(Estado.P);
        mobilidadeEntityRepository.save(mob);

        if (dto.getTipoMobilidade() != null)
          atual.setTipoSituacao(dto.getTipoMobilidade());
        if (dto.getDirecao() != null)
          atual.setInstitId(entityManager.getReference(InstituicaoEntity.class, dto.getDirecao()));
        if (dto.getSecao() != null)
          atual.setSeccaoId(entityManager.getReference(SecaoEntity.class, dto.getSecao()));
        if (dto.getLocalTrabalho() != null)
          atual.setLocTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dto.getLocalTrabalho()));
      }

      var car = atual.getCarreiraId();
      if (dto.getTipoAlteracaoCarreira() != null || dto.getCarreira() != null || dto.getCategoria() != null
          || dto.getEscalao() != null || dto.getSalario() != null || dto.getDataInicioCarreira() != null
          || dto.getDataFimCarreira() != null || dto.getCargo() != null) {
        populateCarreiraCommon(car, dto);
        car.setEstado(Estado.P);
        carreiraEntityRepository.save(car);

        if (dto.getCarreira() != null)
          atual.setCarrPccId(entityManager.getReference(ParamCarreiraEntity.class, dto.getCarreira()));
        if (dto.getCategoria() != null)
          atual.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dto.getCategoria()));
        if (dto.getEscalao() != null)
          atual.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dto.getEscalao()));
        if (dto.getCargo() != null)
          atual.setCargoId(entityManager.getReference(ParamCargoEntity.class, dto.getCargo()));
        if (dto.getSalario() != null)
          atual.setSalario(dto.getSalario());
      }

      var sitLab = atual.getSituacLaboralId();
      if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
          || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
        populateSituacao(sitLab, dto);
        sitLab.setEstado(Estado.P);
        situacaoLaboralEntityRepository.save(sitLab);
      }

      if (dto.getSalario() != null) {
        updateExistingSalaryRemuneracao(funcionario, dto);
      }

      if (dto.getValidar() != null) {
        var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
        updateEstadoOnRelAndChildren(atual, novoEstado);
        updateValidacaoPendentes(funcionario.getUuid(), novoEstado);
      }

      gerarOrdemServicoIfRequested(funcionario, atual, dto);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var hoje = LocalDate.now();
    atual.setEstActAdm(0);
    atual.setDataFim(hoje);

    var novoRelacionamento = dadosContratuaisMapper.clone(atual);
    novoRelacionamento.setEstado(Estado.P);
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setTiprelId(atual);
    novoRelacionamento.setReferente("HISTORICO_LABORAL");
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
      novoRelacionamento.setInstitId(novaMob.getInstidId());
      novoRelacionamento.setSeccaoId(novaMob.getSecaoId());
      novoRelacionamento.setLocTrabId(novaMob.getLocalTrabId());
      novoRelacionamento.setTipoSituacao(dto.getTipoMobilidade());
      criouAlgum = true;

      var validMob = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.MOBILIDADE.name(),
          Estado.P);
      validMob.setFunId(funcionario);
      validMob.setTiprelId(novoRelacionamento);
      funcionario.getValidacoes().add(validMob);
    }

    if (dto.getTipoAlteracaoCarreira() != null || dto.getCarreira() != null || dto.getCategoria() != null
        || dto.getEscalao() != null || dto.getSalario() != null || dto.getDataInicioCarreira() != null
        || dto.getDataFimCarreira() != null || dto.getCargo() != null) {
      var novaCar = new CarreiraEntity();
      populateCarreiraCommon(novaCar, dto);
      novaCar.setFlgProcessa(1);
      novaCar.setEstado(Estado.P);
      novaCar.setObs("HISTORICO_LABORAL");
      novaCar.setUuid(IdentificadorUnico.create().valor());
      novaCar.setContrVinculoId(atual.getContrVinculoId());
      carreiraEntityRepository.save(novaCar);

      novoRelacionamento.setCarreiraId(novaCar);
      novoRelacionamento.setCarrPccId(novaCar.getCarrPccsId());
      novoRelacionamento.setCategoriaId(novaCar.getCategoriaId());
      novoRelacionamento.setEscalaoId(novaCar.getEscalaoId());
      novoRelacionamento.setSalario(novaCar.getSalario());
      criouAlgum = true;

      var validCar = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CARREIRA.name(),
          Estado.P);
      validCar.setFunId(funcionario);
      validCar.setTiprelId(novoRelacionamento);
      funcionario.getValidacoes().add(validCar);

      if (dto.getSalario() != null) {
        closeExistingSalaryAndCreateNew(funcionario, novoRelacionamento, dto);
      }
    }

    if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
        || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
      var novaSit = new SituacaoLaboralEntity();
      populateSituacao(novaSit, dto);
      novaSit.setEstado(Estado.P);
      novaSit.setUuid(IdentificadorUnico.create().valor());
      novaSit.setContrVinculoId(atual.getContrVinculoId());
      situacaoLaboralEntityRepository.save(novaSit);

      novoRelacionamento.setSituacLaboralId(novaSit);
      criouAlgum = true;

      var validSit = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(),
          Referencia.SITUACAO_LABORAL.name(), Estado.P);
      validSit.setFunId(funcionario);
      validSit.setTiprelId(novoRelacionamento);
      funcionario.getValidacoes().add(validSit);
    }

    funcionario.getTiposrelacionamentos().add(novoRelacionamento);

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    saved.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P && v.getTiprelId() != null
            && v.getTiprelId().getId().equals(novoRelacionamento.getId()))
        .forEach(v -> {
          if (Referencia.MOBILIDADE.name().equals(v.getReferenciaName()) && novoRelacionamento.getMobId() != null) {
            v.setReferenciaId(novoRelacionamento.getMobId().getId());
            validacaoEntityRepository.save(v);
          }
          if (Referencia.CARREIRA.name().equals(v.getReferenciaName()) && novoRelacionamento.getCarreiraId() != null) {
            v.setReferenciaId(novoRelacionamento.getCarreiraId().getId());
            validacaoEntityRepository.save(v);
          }
          if (Referencia.SITUACAO_LABORAL.name().equals(v.getReferenciaName())
              && novoRelacionamento.getSituacLaboralId() != null) {
            v.setReferenciaId(novoRelacionamento.getSituacLaboralId().getId());
            validacaoEntityRepository.save(v);
          }
        });

    if (dto.getValidar() != null) {
      var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      updateEstadoOnRelAndChildren(novoRelacionamento, novoEstado);
      updateValidacaoPendentes(funcionario.getUuid(), novoEstado);
    }

    gerarOrdemServicoIfRequested(funcionario, novoRelacionamento, dto);

    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  @Transactional
  public ValidarNovoHistoricoLaboralDTO atualizar(AtualizarRelacaoLaboralCommand command) {
    var dto = command.getValidarnovohistoricolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);
    var relacionamento = tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(command.getHistoricoId()));

    if (relacionamento.getFunId() == null || !relacionamento.getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Histórico laboral não pertence ao funcionário");

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
        relacionamento.setTipoSituacao(dto.getTipoMobilidade());
      if (dto.getDirecao() != null)
        relacionamento.setInstitId(entityManager.getReference(InstituicaoEntity.class, dto.getDirecao()));
      if (dto.getSecao() != null)
        relacionamento.setSeccaoId(entityManager.getReference(SecaoEntity.class, dto.getSecao()));
      if (dto.getLocalTrabalho() != null)
        relacionamento.setLocTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dto.getLocalTrabalho()));
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
      if (dto.getCarreira() != null)
        relacionamento.setCarrPccId(entityManager.getReference(ParamCarreiraEntity.class, dto.getCarreira()));
      if (dto.getCategoria() != null)
        relacionamento.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dto.getCategoria()));
      if (dto.getEscalao() != null)
        relacionamento.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dto.getEscalao()));
      if (dto.getCargo() != null)
        relacionamento.setCargoId(entityManager.getReference(ParamCargoEntity.class, dto.getCargo()));
      if (dto.getSalario() != null)
        relacionamento.setSalario(dto.getSalario());
    }

    if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
        || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
      var sitLab = relacionamento.getSituacLaboralId();
      if (sitLab == null) {
        sitLab = new SituacaoLaboralEntity();
        sitLab.setEstado(Estado.P);
        sitLab.setUuid(IdentificadorUnico.create().valor());
        sitLab.setContrVinculoId(relacionamento.getContrVinculoId());
      }
      populateSituacao(sitLab, dto);
      situacaoLaboralEntityRepository.save(sitLab);
      relacionamento.setSituacLaboralId(sitLab);
    }

    if (dto.getSalario() != null) {
      updateExistingSalaryRemuneracao(funcionario, dto);
    }

    if (dto.getValidar() != null) {
      var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      updateEstadoOnRelAndChildren(relacionamento, novoEstado);
      updateValidacaoPendentes(funcionario.getUuid(), novoEstado);
    }

    gerarOrdemServicoIfRequested(funcionario, relacionamento, dto);

    tiposRelacionamentoEntityRepository.save(relacionamento);
    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  private void populateMobilidade(MobilidadeEntity mob, ValidarNovoHistoricoLaboralDTO dto) {
    if (dto.getTipoMobilidade() != null)
      mob.setTipoSituacao(dto.getTipoMobilidade());
    if (dto.getDirecao() != null)
      mob.setInstidId(entityManager.getReference(InstituicaoEntity.class, dto.getDirecao()));
    if (dto.getSecao() != null)
      mob.setSecaoId(entityManager.getReference(SecaoEntity.class, dto.getSecao()));
    if (dto.getLocalTrabalho() != null)
      mob.setLocalTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dto.getLocalTrabalho()));
    if (dto.getDataInicioMobilidade() != null)
      mob.setDataInicio(dto.getDataInicioMobilidade());
    if (dto.getDataFimMobilidade() != null)
      mob.setDataFim(dto.getDataFimMobilidade());
  }

  private void populateCarreiraCommon(CarreiraEntity car, ValidarNovoHistoricoLaboralDTO dto) {
    if (dto.getCargo() != null)
      car.setCargoId(entityManager.getReference(ParamCargoEntity.class, dto.getCargo()));
    if (dto.getEscalao() != null)
      car.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dto.getEscalao()));
    if (dto.getCategoria() != null)
      car.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dto.getCategoria()));
    if (dto.getCarreira() != null)
      car.setCarrPccsId(entityManager.getReference(ParamCarreiraEntity.class, dto.getCarreira()));
    if (dto.getSalario() != null)
      car.setSalario(dto.getSalario());
    else if (car.getSalario() == null)
      car.setSalario(BigDecimal.ZERO);
    car.setTipoSituacao(dto.getTipoAlteracaoCarreira());
    car.setDataInicio(dto.getDataInicioCarreira());
    car.setDataFim(dto.getDataFimCarreira());
  }

  private void populateSituacao(SituacaoLaboralEntity sit, ValidarNovoHistoricoLaboralDTO dto) {
    if (dto.getSituacaoLaboral() != null)
      sit.setSituacaoLaboralId(entityManager.getReference(ParamSituacaoEntity.class, dto.getSituacaoLaboral()));
    if (dto.getMotivo() != null) {
      try {
        var mid = Long.parseLong(dto.getMotivo());
        sit.setMotivoSitLabId(entityManager.getReference(ParamSituacaoDetalheEntity.class, mid));
      } catch (NumberFormatException ignored) {
      }
      sit.setMotivoSitLab(dto.getMotivo());
    }
    sit.setDataInicio(dto.getDataInicioSituacao());
    sit.setDataFim(dto.getDataFimSituacao());
    sit.setObs(dto.getObservacao());
  }

  private void updateExistingSalaryRemuneracao(FuncionarioEntity funcionario, ValidarNovoHistoricoLaboralDTO dto) {
    var tmSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
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

  private void closeExistingSalaryAndCreateNew(FuncionarioEntity funcionario, TiposRelacionamentoEntity rel,
      ValidarNovoHistoricoLaboralDTO dto) {
    var tmSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
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

  private void gerarOrdemServicoIfRequested(FuncionarioEntity funcionario, TiposRelacionamentoEntity rel,
      ValidarNovoHistoricoLaboralDTO dto) {
    if (dto.getGerarOrdemServico() != null && ("SIM".equalsIgnoreCase(dto.getGerarOrdemServico())
        || "TRUE".equalsIgnoreCase(dto.getGerarOrdemServico()))) {
      var numero = "OS-" + System.currentTimeMillis();
      var ordem = new OrdemServicoEntity();
      ordem.setNuOrdem(numero);
      ordem.setDescricao(dto.getOrdemServico());
      ordem.setReferente("HISTORICO_LABORAL");
      ordem.setEstado(Estado.A);
      ordem.setUuid(IdentificadorUnico.create().valor());
      ordem.setFunId(funcionario);
      ordem.setTiprelId(rel);
      funcionario.getOrdemServicos().add(ordem);
    }
  }

}
