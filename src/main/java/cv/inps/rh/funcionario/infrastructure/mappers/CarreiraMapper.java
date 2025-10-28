package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Carreira;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCargoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCarreiraMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCategoriaMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamEscalaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarreiraMapper {

  private final ContratoMapper contratoMapper;
  private final ParamCargoMapper paramCargoMapper;
  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamCarreiraMapper paramCarreiraMapper;

  private final EntityManager entityManager;

  // Entity -> Domain
  public Carreira toDomain(CarreiraEntity entity) {
    if (entity == null) return null;

    return Carreira.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getSalario(),
        entity.getFlgProcessa(),
        entity.getTipoSituacao(),
        entity.getEstado(),
        entity.getObs(),
        contratoMapper.toDomain(entity.getContratoId()),
        entity.getCargoId() != null ? paramCargoMapper.toDomain(entity.getCargoId()) : null,
        entity.getEscalaoId() != null ? paramEscalaoMapper.toDomain(entity.getEscalaoId()) : null,
        entity.getCategoriaId() != null ? paramCategoriaMapper.toDomain(entity.getCategoriaId()) : null,
        entity.getCarrPccsId() != null ? paramCarreiraMapper.toDomain(entity.getCarrPccsId()) : null
    );
  }

  // Domain -> Entity
  public CarreiraEntity toEntity(Carreira domain) {
    if (domain == null) return null;

    CarreiraEntity entity = new CarreiraEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setSalario(domain.getSalario());
    entity.setFlgProcessa(domain.getFlgProcessa());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setContratoId(contratoMapper.toEntity(domain.getContrato()));

    // By reference ou via mapper
    entity.setCargoId(domain.getCargo() != null
        ? entityManager.getReference(ParamCargoEntity.class, domain.getCargo().getId())
        : null);
    entity.setEscalaoId(domain.getEscalao() != null
        ? entityManager.getReference(ParamEscalaoEntity.class, domain.getEscalao().getId())
        : null);
    entity.setCategoriaId(domain.getCategoria() != null
        ? entityManager.getReference(ParamCategoriaEntity.class, domain.getCategoria().getId())
        : null);
    entity.setCarrPccsId(domain.getCarrPccs() != null
        ? entityManager.getReference(ParamCarreiraEntity.class, domain.getCarrPccs().getId())
        : null);

    return entity;
  }

}
