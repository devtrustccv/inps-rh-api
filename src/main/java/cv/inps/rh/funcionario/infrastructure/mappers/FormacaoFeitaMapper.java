package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FormacaoFeitaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormacaoFeitaMapper {

  private final EntityManager entityManager;


  public FormacaoFeitaEntity toEntity(FormacaoProfissionalReqDTO dto, Estado estado) {
    if (dto == null) {
      return null;
    }
    FormacaoFeitaEntity e = new FormacaoFeitaEntity();

    if(dto.getId() !=null && dto.getId()>0)
     e.setId(dto.getId());

    // Referência ao país
    if (dto.getPaisId() != null) {
      e.setPaisId(entityManager.getReference(GeografiaEntity.class, dto.getPaisId()));
    }

    e.setEstabelecimento(dto.getEstabelecimento());
    e.setRhtpfor(dto.getTipoFormacao());
    e.setCurso(dto.getDesignacao());
    e.setNivel(dto.getNivel());
    e.setEstado(estado);

    return e;
  }

  public java.util.List<FormacaoFeitaEntity> syncFormacoes(java.util.List<FormacaoFeitaEntity> existingList,
                            java.util.List<FormacaoProfissionalReqDTO> newList) {
    if (newList == null) return existingList;
    for (FormacaoProfissionalReqDTO dto : newList) {
      FormacaoFeitaEntity found = null;
      if (dto.getId() != null) {
        for (FormacaoFeitaEntity f : existingList) {
          if (java.util.Objects.equals(f.getId(), dto.getId())) { found = f; break; }
        }
      }
      if (found != null) {
        if (dto.getPaisId() != null) {
          found.setPaisId(entityManager.getReference(GeografiaEntity.class, dto.getPaisId()));
        }
        found.setEstabelecimento(dto.getEstabelecimento());
        found.setRhtpfor(dto.getTipoFormacao());
        found.setCurso(dto.getDesignacao());
        found.setNivel(dto.getNivel());
      } else {
        FormacaoFeitaEntity novo = toEntity(dto, Estado.P);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (FormacaoFeitaEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


}
