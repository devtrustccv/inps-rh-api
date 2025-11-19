package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.domain.models.Familiar;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FamiliarMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;


  private final EntityManager em;

  public Familiar toDomain(FamiliarEntity entity) {
    if (entity == null) return null;

    return Familiar.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTpDocumento() != null ? tipoDocumentoMapper.toDomain(entity.getTpDocumento()) : null,
        entity.getNumDocumento(),
        entity.getNome(),
        entity.getDataNascimento(),
        entity.getSexo(),
        entity.getGdpId(),
        entity.getDependencia(),
        entity.getMembroAgr(),
        entity.getNmPai(),
        entity.getNmMae(),
        entity.getEstado()
    );
  }

  public FamiliarEntity toEntity(Familiar familiar) {
    if (familiar == null) return null;

    FamiliarEntity entity = new FamiliarEntity();

    if (familiar.getId() != null) {
      entity.setId(familiar.getId());
    }

    entity.setUuid(familiar.getUuid() != null ? familiar.getUuid().getValor() : null);
    if (familiar.getTipoDocumento() != null) {
      entity.setTpDocumento(em.getReference(TipoDocumentoEntity.class, familiar.getTipoDocumento().getId()));
    }

    entity.setNumDocumento(familiar.getNumDocumento());
    entity.setNome(familiar.getNome());
    entity.setDataNascimento(familiar.getDataNascimento());
    entity.setSexo(familiar.getSexo());
    entity.setGdpId(familiar.getGrauParentesco());
    entity.setDependencia(familiar.getDependencia());
    entity.setMembroAgr(familiar.getMembroAgr());
    entity.setNmPai(familiar.getNmPai());
    entity.setNmMae(familiar.getNmMae());
    entity.setEstado(familiar.getEstado());

    return entity;
  }

  public Familiar toDomain(AgregadoDependenteReqDTO dto) {
    if (dto == null) return null;

    return Familiar.create(
        dto.getId(),
        dto.getTipoDocumentoId() != null ? tipoDocumentoMapper.toDomain(dto.getTipoDocumentoId()) : null,
        dto.getNumDocumento(),
        dto.getNome(),
        dto.getDataNascimento(),
        dto.getGenero(),
        dto.getGrauParentesco(),
        dto.getDependente(),
        dto.getAgregada(),
        null, // nmPai
        null  // nmMae
    );
  }

  public List<Familiar> toFamiliaresDomain(List<AgregadoDependenteReqDTO> agregadoDependenteReqDTOS) {
    if (agregadoDependenteReqDTOS == null) return null;
    return agregadoDependenteReqDTOS.stream()
        .map(this::toDomain)
        .toList();
  }

  public AgregadoDependenteRespDTO toResponseDTO(Familiar familiar) {
    if (familiar == null) return null;

    AgregadoDependenteRespDTO dto = new AgregadoDependenteRespDTO();

    dto.setId(familiar.getId());
    dto.setTipoDocumentoId(familiar.getTipoDocumento() != null ? familiar.getTipoDocumento().getId() : null);
    dto.setTipoDocumentoDesc(familiar.getTipoDocumento() != null ? familiar.getTipoDocumento().getNome() : null);
    dto.setNumDocumento(familiar.getNumDocumento());
    dto.setNome(familiar.getNome());
    dto.setDataNascimento(familiar.getDataNascimento());
    dto.setGenero(familiar.getSexo());
    dto.setGrauParentesco(familiar.getGrauParentesco());
    dto.setDependente(familiar.getDependencia());
    dto.setAgregada(familiar.getMembroAgr());
    dto.setEstado(familiar.getEstado() != null ? familiar.getEstado().name() : null);

    return dto;
  }

  public FamiliarEntity toEntity(AgregadoDependenteReqDTO dto, Estado estado) {
    if (dto == null) return null;
    FamiliarEntity entity = new FamiliarEntity();
    if (dto.getTipoDocumentoId() != null) {
      entity.setTpDocumento(em.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
    }
    entity.setNumDocumento(dto.getNumDocumento());
    entity.setNome(dto.getNome());
    entity.setDataNascimento(dto.getDataNascimento());
    entity.setSexo(dto.getGenero());
    entity.setGdpId(dto.getGrauParentesco());
    entity.setDependencia(dto.getDependente());
    entity.setMembroAgr(dto.getAgregada());
    entity.setEstado(estado);
    return entity;
  }

  public java.util.List<FamiliarEntity> syncFamiliares(java.util.List<FamiliarEntity> existingList,
                             java.util.List<AgregadoDependenteReqDTO> newList) {
    if (newList == null) return existingList;
    for (AgregadoDependenteReqDTO dto : newList) {
      FamiliarEntity found = null;
      if (dto.getId() != null) {
        for (FamiliarEntity f : existingList) {
          if (java.util.Objects.equals(f.getId(), dto.getId())) { found = f; break; }
        }
      }
      if (found != null) {
        if (dto.getTipoDocumentoId() != null) {
          found.setTpDocumento(em.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
        }
        found.setNumDocumento(dto.getNumDocumento());
        found.setNome(dto.getNome());
        found.setDataNascimento(dto.getDataNascimento());
        found.setSexo(dto.getGenero());
        found.setGdpId(dto.getGrauParentesco());
        found.setDependencia(dto.getDependente());
        found.setMembroAgr(dto.getAgregada());
      } else {
        FamiliarEntity novo = toEntity(dto, Estado.P);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (FamiliarEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }



  public List<AgregadoDependenteRespDTO> toResponseDTOList(List<Familiar> familiares) {
    if (familiares == null) return null;
    return familiares.stream()
        .map(this::toResponseDTO)
        .toList();
  }








}
