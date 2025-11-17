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
        entity.getTpContrato(),
        entity.getEstActAdm()
    );
  }

  public TiposRelacionamentoEntity toEntity(TiposRelacionamento domain) {
    if (domain == null) return null;

    TiposRelacionamentoEntity entity = new TiposRelacionamentoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());

    entity.setCargoId(getReferenceIfNotNull(ParamCargoEntity.class,
        domain.getCargo() != null ? domain.getCargo().getId() : null));
    entity.setInstitId(getReferenceIfNotNull(InstituicaoEntity.class,
        domain.getInstituicao() != null ? domain.getInstituicao().getId() : null));
    entity.setVinculoId(getReferenceIfNotNull(ParamVinculoEntity.class,
        domain.getVinculo() != null ? domain.getVinculo().getId() : null));
    entity.setSeccaoId(getReferenceIfNotNull(SecaoEntity.class,
        domain.getSeccao() != null ? domain.getSeccao().getId() : null));
    entity.setCategoriaId(getReferenceIfNotNull(ParamCategoriaEntity.class,
        domain.getCategoria() != null ? domain.getCategoria().getId() : null));
    entity.setEscalaoId(getReferenceIfNotNull(ParamEscalaoEntity.class,
        domain.getEscalao() != null ? domain.getEscalao().getId() : null));
    entity.setCarrPccId(getReferenceIfNotNull(ParamCarreiraEntity.class,
        domain.getCarrPcc() != null ? domain.getCarrPcc().getId() : null));
    entity.setLocTrabId(getReferenceIfNotNull(ParamLocalTrabEntity.class,
        domain.getLocTrab() != null ? domain.getLocTrab().getId() : null));
    entity.setSituacLaboralId(getReferenceIfNotNull(ParamSitLaboralEntity.class,
        domain.getSituacLaboral() != null ? domain.getSituacLaboral().getId() : null));

    var tipoContrato = getReferenceIfNotNull(ParamContratoEntity.class,
        domain.getTipoContrato() != null ? domain.getTipoContrato().getId() : null);
    entity.setTipoContratoId(tipoContrato);

    entity.setTpContrato(tipoContrato != null ? tipoContrato.getNome() : null);

    entity.setSalario(domain.getSalario());
    entity.setMoeda(domain.getMoeda());
    entity.setRegime(domain.getRegime());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setTiprelId(domain.getTiprelAnterior()!=null ? getReferenceIfNotNull(TiposRelacionamentoEntity.class, domain.getTiprelAnterior().getId()) : null);
    entity.setFlgProcessa(domain.getFlgProcessa());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDataInicioContrato(domain.getDataInicioContrato());
    entity.setDataFimContrato(domain.getDataFimContrato());
    entity.setReferente(domain.getReferente());
    entity.setUltProc(domain.getUltProc());
    entity.setMotivoSitLab(domain.getMotivoSitLab());

    entity.setEstActAdm(domain.getEstadoActividadeAdm());

    return entity;
  }



  private <T> T getReferenceIfNotNull(Class<T> clazz, Object id) {
    if (id == null) return null;
    return entityManager.getReference(clazz, id);
  }


}
