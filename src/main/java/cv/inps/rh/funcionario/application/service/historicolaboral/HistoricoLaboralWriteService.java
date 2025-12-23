package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.commands.ValidarHistoricoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.ValidarDadosContratuaisService;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
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
import java.util.List;

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
  public ValidarNovoHistoricoLaboralDTO validar(ValidarHistoricoLaboralCommand command) {

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
        if (dto.getTipoAlteracaoCarreira() != null)
          car.setTipoSituacao(dto.getTipoAlteracaoCarreira());
        if (dto.getCarreira() != null)
          car.setCarrPccsId(entityManager.getReference(ParamCarreiraEntity.class, dto.getCarreira()));
        if (dto.getCategoria() != null)
          car.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dto.getCategoria()));
        if (dto.getEscalao() != null)
          car.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dto.getEscalao()));
        if (dto.getCargo() != null)
          car.setCargoId(entityManager.getReference(ParamCargoEntity.class, dto.getCargo()));
        if (dto.getSalario() != null)
          car.setSalario(dto.getSalario());
        if (dto.getDataInicioCarreira() != null)
          car.setDataInicio(dto.getDataInicioCarreira());
        if (dto.getDataFimCarreira() != null)
          car.setDataFim(dto.getDataFimCarreira());
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
        if (dto.getSituacaoLaboral() != null)
          sitLab.setSituacaoLaboralId(entityManager.getReference(ParamSituacaoEntity.class, dto.getSituacaoLaboral()));
        if (dto.getMotivo() != null) {
          try {
            var mid = Long.parseLong(dto.getMotivo());
            sitLab.setMotivoSitLabId(entityManager.getReference(ParamSituacaoDetalheEntity.class, mid));
          } catch (NumberFormatException ignored) {
          }
          sitLab.setMotivoSitLab(dto.getMotivo());
        }
        if (dto.getDataInicioSituacao() != null)
          sitLab.setDataInicio(dto.getDataInicioSituacao());
        if (dto.getDataFimSituacao() != null)
          sitLab.setDataFim(dto.getDataFimSituacao());
        if (dto.getObservacao() != null)
          sitLab.setObs(dto.getObservacao());
        sitLab.setEstado(Estado.P);
        situacaoLaboralEntityRepository.save(sitLab);
      }

      if (dto.getSalario() != null) {
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

      if (dto.getValidar() != null) {
        var novoEstado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
        atual.setEstado(novoEstado);
        var contrato = atual.getContrVinculoId();
        if (contrato != null) {
          contrato.setEstado(novoEstado);
        }
        if (atual.getMobId() != null)
          atual.getMobId().setEstado(novoEstado);
        if (atual.getCarreiraId() != null)
          atual.getCarreiraId().setEstado(novoEstado);
        if (atual.getSituacLaboralId() != null)
          atual.getSituacLaboralId().setEstado(novoEstado);

        funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)
            .ifPresent(v -> v.setEstado(novoEstado));
        funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
            .ifPresent(v -> v.setEstado(novoEstado));
        funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.SITUACAO_LABORAL)
            .ifPresent(v -> v.setEstado(novoEstado));
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
    novoRelacionamento.setUltProc(hoje);

    boolean criouAlgum = false;

    if (dto.getTipoMobilidade() != null || dto.getDirecao() != null || dto.getSecao() != null
        || dto.getLocalTrabalho() != null || dto.getDataInicioMobilidade() != null
        || dto.getDataFimMobilidade() != null) {
      var novaMob = new MobilidadeEntity();
      novaMob.setTipoSituacao(dto.getTipoMobilidade());
      novaMob.setEstado(Estado.P);
      novaMob.setObs("HISTORICO_LABORAL");
      novaMob.setUuid(IdentificadorUnico.create().valor());
      if (dto.getDirecao() != null)
        novaMob.setInstidId(entityManager.getReference(InstituicaoEntity.class, dto.getDirecao()));
      if (dto.getSecao() != null)
        novaMob.setSecaoId(entityManager.getReference(SecaoEntity.class, dto.getSecao()));
      if (dto.getLocalTrabalho() != null)
        novaMob.setLocalTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dto.getLocalTrabalho()));
      novaMob.setDataInicio(dto.getDataInicioMobilidade());
      novaMob.setDataFim(dto.getDataFimMobilidade());
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
      if (dto.getCargo() != null)
        novaCar.setCargoId(entityManager.getReference(ParamCargoEntity.class, dto.getCargo()));
      if (dto.getEscalao() != null)
        novaCar.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dto.getEscalao()));
      if (dto.getCategoria() != null)
        novaCar.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dto.getCategoria()));
      if (dto.getCarreira() != null)
        novaCar.setCarrPccsId(entityManager.getReference(ParamCarreiraEntity.class, dto.getCarreira()));
      novaCar.setSalario(dto.getSalario() != null ? dto.getSalario() : BigDecimal.ZERO);
      novaCar.setFlgProcessa(1);
      novaCar.setTipoSituacao(dto.getTipoAlteracaoCarreira());
      novaCar.setEstado(Estado.P);
      novaCar.setObs("HISTORICO_LABORAL");
      novaCar.setUuid(IdentificadorUnico.create().valor());
      novaCar.setDataInicio(dto.getDataInicioCarreira());
      novaCar.setDataFim(dto.getDataFimCarreira());
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
        var tmSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
        var renumSal = definicaoRemuneracaoMapper.createRenumeracao(dto.getSalario(), tmSalario,
            dto.getDataInicioCarreira(), dto.getDataFimCarreira(), funcionario, null);
        definicaoRemuneracaoEntityRepository.save(renumSal);
        var remun = new RemuneracaoTiprelEntity();
        remun.setEstado(Estado.P);
        remun.setUuid(IdentificadorUnico.create().valor());
        remun.setRemId(renumSal);
        remun.setTiprelId(novoRelacionamento);
        remuneracaoTiprelEntityRepository.save(remun);
      }
    }

    if (dto.getSituacaoLaboral() != null || dto.getMotivo() != null || dto.getDataInicioSituacao() != null
        || dto.getDataFimSituacao() != null || dto.getObservacao() != null) {
      var novaSit = new SituacaoLaboralEntity();
      if (dto.getSituacaoLaboral() != null)
        novaSit.setSituacaoLaboralId(entityManager.getReference(ParamSituacaoEntity.class, dto.getSituacaoLaboral()));
      if (dto.getMotivo() != null) {
        try {
          var mid = Long.parseLong(dto.getMotivo());
          novaSit.setMotivoSitLabId(entityManager.getReference(ParamSituacaoDetalheEntity.class, mid));
        } catch (NumberFormatException ignored) {
        }
        novaSit.setMotivoSitLab(dto.getMotivo());
      }
      novaSit.setDataInicio(dto.getDataInicioSituacao());
      novaSit.setDataFim(dto.getDataFimSituacao());
      novaSit.setEstado(Estado.P);
      novaSit.setObs(dto.getObservacao());
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
      novoRelacionamento.setEstado(novoEstado);
      if (novoRelacionamento.getMobId() != null)
        novoRelacionamento.getMobId().setEstado(novoEstado);
      if (novoRelacionamento.getCarreiraId() != null)
        novoRelacionamento.getCarreiraId().setEstado(novoEstado);
      if (novoRelacionamento.getSituacLaboralId() != null)
        novoRelacionamento.getSituacLaboralId().setEstado(novoEstado);

      funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)
          .ifPresent(v -> v.setEstado(novoEstado));
      funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
          .ifPresent(v -> v.setEstado(novoEstado));
      funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.SITUACAO_LABORAL)
          .ifPresent(v -> v.setEstado(novoEstado));
    }

    funcionarioEntityRepository.save(funcionario);
    return dto;
  }

}
