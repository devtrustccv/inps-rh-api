package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FamiliarMapper {

  private final EntityManager em;


  public FamiliarEntity toEntity(AgregadoDependenteReqDTO dto, Estado estado, FuncionarioEntity fun) {
    if (dto == null) return null;
    FamiliarEntity entity = new FamiliarEntity();
    if (dto.getTipoDocumentoId() != null) {
      entity.setTpDocumentoId(em.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
    }
    entity.setNumDocumento(dto.getNumDocumento());
    entity.setNome(dto.getNome());
    entity.setDataNascimento(dto.getDataNascimento());
    entity.setSexo(dto.getGenero());
    entity.setGdpId(dto.getGrauParentesco());
    entity.setDependencia(dto.getDependente());
    entity.setMembroAgr(dto.getAgregada());
    entity.setEstado(estado);
    entity.setFunId(fun);
    return entity;
  }

  public java.util.List<FamiliarEntity> syncFamiliares(java.util.List<FamiliarEntity> existingList,
                             java.util.List<AgregadoDependenteReqDTO> newList, FuncionarioEntity fun) {
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
          found.setTpDocumentoId(em.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
        }
        found.setNumDocumento(dto.getNumDocumento());
        found.setNome(dto.getNome());
        found.setDataNascimento(dto.getDataNascimento());
        found.setSexo(dto.getGenero());
        found.setGdpId(dto.getGrauParentesco());
        found.setDependencia(dto.getDependente());
        found.setMembroAgr(dto.getAgregada());
      } else {
        FamiliarEntity novo = toEntity(dto, Estado.P, fun);
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


  public List<AgregadoDependenteRespDTO> toAgregadoDependenteRespDTOList(List<FamiliarEntity> familiares) {
   return familiares.stream().map(f -> {
      AgregadoDependenteRespDTO fr = new AgregadoDependenteRespDTO();
      fr.setId(f.getId());
      fr.setTipoDocumentoId(f.getTpDocumentoId() != null ? f.getTpDocumentoId().getId() : null);
      fr.setTipoDocumentoDesc(f.getTpDocumentoId() != null ? f.getTpDocumentoId().getNome() : null);
      fr.setNumDocumento(f.getNumDocumento());
      fr.setNome(f.getNome());
      fr.setDataNascimento(f.getDataNascimento());
      fr.setGenero(f.getSexo());
      fr.setGrauParentesco(f.getGdpId());
      fr.setDependente(f.getDependencia());
      fr.setAgregada(f.getMembroAgr());
      fr.setEstado(f.getEstado() != null ? f.getEstado().name() : null);
      return fr;
    }).toList();
  }
}
