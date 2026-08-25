package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
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
    entity.setTpDocumentoId(ValidationUtil.ref(em, TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
    entity.setNumDocumento(ValidationUtil.trimToNull(dto.getNumDocumento()));
    entity.setNome(ValidationUtil.trimToNull(dto.getNome()));
    entity.setDataNascimento(dto.getDataNascimento());
    entity.setSexo(ValidationUtil.trimToNull(dto.getGenero()));
    entity.setGdpId(dto.getGrauParentesco());
    entity.setDependencia(dto.getDependente());
    entity.setMembroAgr(dto.getAgregada());
    entity.setResponsavel(dto.getResponsavel());
    entity.setEstado(estado);
    entity.setFunId(fun);
    return entity;
  }

  public java.util.List<FamiliarEntity> syncFamiliares(java.util.List<FamiliarEntity> existingList,
                             java.util.List<AgregadoDependenteReqDTO> newList, FuncionarioEntity fun, Estado estadoParaNovos) {
    if (newList == null) return existingList;
    for (AgregadoDependenteReqDTO dto : newList) {
      FamiliarEntity found = null;
      if (dto.getId() != null) {
        for (FamiliarEntity f : existingList) {
          if (java.util.Objects.equals(f.getId(), dto.getId())) { found = f; break; }
        }
      }
      if (found != null) {
        found.setTpDocumentoId(ValidationUtil.ref(em, TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
        found.setNumDocumento(ValidationUtil.trimToNull(dto.getNumDocumento()));
        found.setNome(ValidationUtil.trimToNull(dto.getNome()));
        found.setDataNascimento(dto.getDataNascimento());
        found.setSexo(ValidationUtil.trimToNull(dto.getGenero()));
        found.setGdpId(dto.getGrauParentesco());
        found.setDependencia(dto.getDependente());
        found.setMembroAgr(dto.getAgregada());
        found.setResponsavel(dto.getResponsavel());
      } else {
        FamiliarEntity novo = toEntity(dto, estadoParaNovos, fun);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (FamiliarEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
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
     fr.setResponsavel(f.getResponsavel());
      fr.setEstado(f.getEstado() != null ? f.getEstado().getCode() : null);
      fr.setEstadoDesc(f.getEstado() != null ? f.getEstado().getDescription() : null);
      return fr;
    }).toList();
  }
}
