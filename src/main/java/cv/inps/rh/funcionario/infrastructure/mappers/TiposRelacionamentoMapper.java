package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
import cv.inps.rh.parametrizacao.infrastructure.mappers.*;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TiposRelacionamentoMapper {

  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final ParamCargoMapper paramCargoMapper;
  private final ParamVinculoMapper paramVinculoMapper;
  private final SecaoMapper secaoMapper;
  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamCarreiraMapper paramCarreiraMapper;
  private final ParamLocalTrabMapper paramLocalTrabMapper;
  private final ParamContratoMapper paramContratoMapper;
  private final ParamSitLaboralMapper paramSitLaboralMapper;
  private final InstituicaoMapper instituicaoMapper;

  private final EntityManager entityManager;

  // Entity -> Domain
  public TiposRelacionamento toDomain(TiposRelacionamentoEntity entity) {
    if (entity == null) return null;

    return TiposRelacionamento.rebuild(
        entity.getId(),
        entity.getUuid(),
        paramCargoMapper.toDomain(entity.getCargoId()),
        instituicaoMapper.toDomain(entity.getInstitId()),
        paramVinculoMapper.toDomain(entity.getVinculoId()),
        secaoMapper.toDomain(entity.getSeccaoId()),
        paramCategoriaMapper.toDomain(entity.getCategoriaId()),
        paramEscalaoMapper.toDomain(entity.getEscalaoId()),
        paramCarreiraMapper.toDomain(entity.getCarrPccId()),
        entity.getSalario(),
        entity.getMoeda(),
        entity.getRegime(),
        entity.getTipoSituacao(),
        null /*toDomain(entity.getTiprelId())*/,
        entity.getFlgProcessa(),
        entity.getEstado(),
        entity.getObs(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getDataInicioContrato(),
        entity.getDataFimContrato(),
        contratoMapper.toDomain(entity.getContratoId()),
        carreiraMapper.toDomain(entity.getCarreiraId()),
        mobilidadeMapper.toDomain(entity.getMobId()),
        paramLocalTrabMapper.toDomain(entity.getLocTrabId()),
        regimeTrabalhoMapper.toDomain(entity.getRegimeId()),
        paramContratoMapper.toDomain(entity.getTipoContratoId()),
        entity.getReferente(),
        entity.getUltProc(),
        entity.getMotivoSitLab(),
        paramSitLaboralMapper.toDomain(entity.getSituacLaboralId()),
        entity.getTpContrato()
    );
  }

  // Domain -> Entity
  public TiposRelacionamentoEntity toEntity(TiposRelacionamento domain) {
    if (domain == null) return null;

    TiposRelacionamentoEntity entity = new TiposRelacionamentoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setCargoId(entityManager.getReference(ParamCargoEntity.class, domain.getCargo().getId()));
    entity.setInstitId(entityManager.getReference(InstituicaoEntity.class, domain.getInstituicao().getId()));
    entity.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, domain.getVinculo().getId()));
    entity.setSeccaoId(entityManager.getReference(SecaoEntity.class, domain.getSeccao().getId()));
    entity.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, domain.getCategoria().getId()));
    entity.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, domain.getEscalao().getId()));
    entity.setCarrPccId(entityManager.getReference(ParamCarreiraEntity.class, domain.getCarrPcc().getId()));
    entity.setSalario(domain.getSalario());
    entity.setMoeda(domain.getMoeda());
    entity.setRegime(domain.getRegime());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setTiprelId(toEntity(domain.getTiprelAnterior()));
    entity.setFlgProcessa(domain.getFlgProcessa());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDataInicioContrato(domain.getDataInicioContrato());
    entity.setDataFimContrato(domain.getDataFimContrato());
    //entity.setContratoId(contratoMapper.toEntity(domain.getContrato()));
    //entity.setCarreiraId(carreiraMapper.toEntity(domain.getCarreira()));
    //entity.setMobId(mobilidadeMapper.toEntity(domain.getMobilidade()));
    entity.setLocTrabId(entityManager.getReference(ParamLocalTrabEntity.class, domain.getLocTrab().getId()));


   /* var regimeTrabalho =regimeTrabalhoMapper.toEntity(domain.getRegimeTrabalho());
    regimeTrabalho.setTiprelId(entity);
    entity.setRegimeId(regimeTrabalho);*/

    entity.setTipoContratoId(entityManager.getReference(ParamContratoEntity.class, domain.getTipoContrato().getId()));
    entity.setReferente(domain.getReferente());
    entity.setUltProc(domain.getUltProc());
    entity.setMotivoSitLab(domain.getMotivoSitLab());
    entity.setSituacLaboralId(entityManager.getReference(ParamSitLaboralEntity.class, domain.getSituacLaboral().getId()));
    entity.setTpContrato(domain.getTpContrato());

    return entity;
  }

}
