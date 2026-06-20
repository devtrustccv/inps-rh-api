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

  @Transactional
  public RelacaoLaboralDTO validar(NovaRelacaoLaboralCommand command) {

    var dto = command.getRelacaolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var flgProc = atual.getFlgProcessa();

    if (flgProc != null) {
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

        var cargoRef = ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getCargo());
        if (cargoRef != null) atual.setCargoId(cargoRef);
        if (dto.getSalario() != null)
          atual.setSalario(dto.getSalario());
      }

      var sitLab = atual.getSituacLaboralId();
      if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
          || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
        if (sitLab == null) {
          sitLab = new SituacaoLaboralEntity();
          sitLab.setEstado(Estado.P);
          sitLab.setUuid(IdentificadorUnico.create().valor());
          sitLab.setContrVinculoId(atual.getContrVinculoId());
          atual.setSituacLaboralId(sitLab);
        }
        populateSituacao(sitLab, dto);
        sitLab.setEstado(Estado.P);
        situacaoLaboralEntityRepository.save(sitLab);
      }

      if (dto.getSalario() != null) {
        updateExistingSalaryRemuneracao(funcionario, dto, atual);
      }

      if (dto.getValidar() != null) {
        var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
        updateEstadoOnRelAndChildren(atual, novoEstado);
        updateValidacaoPendentes(funcionario.getUuid(), novoEstado);
        if (novoEstado == Estado.A) {
          ordemServicoWriteService.criar(funcionario, atual, dto.getTipoOrdemServico());
        }
      }

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
      validSit.setReferenciaUuid(novaSit.getUuid());
      funcionario.getValidacoes().add(validSit);
    }

    funcionario.getTiposrelacionamentos().add(novoRelacionamento);

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(atual, novoRelacionamento, novasRemuneracoes, Collections.emptyList());

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
      if (novoEstado == Estado.A) {
        ordemServicoWriteService.criar(funcionario, novoRelacionamento, dto.getTipoOrdemServico());
      }
    }

    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  @Transactional
  public RelacaoLaboralDTO atualizar(AtualizarRelacaoLaboralCommand command) {
    var dto = command.getRelacaolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var relacionamento = tiposRelacionamentoEntityRepository.findByCarreiraId_uuid(UUID.fromString(command.getCarreiraId()));
    if (relacionamento == null) {
      throw IgrpResponseStatusException.notFound("Histórico Laboral não encontrado");
    }

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
        sitLab.setEstado(Estado.P);
        sitLab.setUuid(IdentificadorUnico.create().valor());
        sitLab.setContrVinculoId(relacionamento.getContrVinculoId());
      }
      populateSituacao(sitLab, dto);
      situacaoLaboralEntityRepository.save(sitLab);
      relacionamento.setSituacLaboralId(sitLab);
    }

    if (dto.getSalario() != null) {
      updateExistingSalaryRemuneracao(funcionario, dto,relacionamento);
    }

    if (dto.getValidar() != null) {
      var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      updateEstadoOnRelAndChildren(relacionamento, novoEstado);
      updateValidacaoPendentes(funcionario.getUuid(), novoEstado);
      if (novoEstado == Estado.A) {
        ordemServicoWriteService.criar(funcionario, relacionamento, dto.getTipoOrdemServico());
      }
    }

    tiposRelacionamentoEntityRepository.save(relacionamento);
    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

  private void populateMobilidade(MobilidadeEntity mob, RelacaoLaboralDTO dto) {
    if (dto.getTipoMobilidade() != null)
      mob.setTipoSituacao(dto.getTipoMobilidade());
    var instRef = ValidationUtil.ref(entityManager, InstituicaoEntity.class, dto.getDirecao());
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
    car.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoAlteracaoCarreira()));
    car.setDataInicio(dto.getDataInicioCarreira());
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
    sit.setDataInicio(dto.getDataInicioSituacao());
    sit.setDataFim(dto.getDataFimSituacao());
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
