package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamiliarMapper {

  private final EntityManager em;


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






}
