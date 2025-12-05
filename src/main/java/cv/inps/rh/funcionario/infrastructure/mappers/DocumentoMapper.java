package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AnexoReqDTO;
import cv.inps.rh.funcionario.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentoMapper {

  private final EntityManager entityManager;


  public DocumentoEntity toEntity(AnexoReqDTO dto, Estado estado) {
    if (dto == null) return null;
    DocumentoEntity entity = new DocumentoEntity();
    entity.setTpDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
    entity.setReferenciaName(dto.getDocumento());
    entity.setReferenciaId(dto.getDocumento());
    entity.setEstado(estado);
    entity.setDocId(1L);
    return entity;
  }

  public java.util.List<DocumentoEntity> syncDocumentos(java.util.List<DocumentoEntity> existingList,
                             java.util.List<AnexoReqDTO> newList) {
    if (newList == null) return existingList;
    for (AnexoReqDTO dto : newList) {
      DocumentoEntity found = null;
      if (dto.getId() != null) {
        for (DocumentoEntity d : existingList) {
          if (java.util.Objects.equals(d.getId(), dto.getId())) { found = d; break; }
        }
      }
      if (found != null) {
        if (dto.getTipoDocumentoId() != null) {
          found.setTpDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dto.getTipoDocumentoId()));
        }
        found.setReferenciaName(dto.getDocumento());
        found.setReferenciaId(dto.getDocumento());
      } else {
        DocumentoEntity novo = toEntity(dto, Estado.P);
        existingList.add(novo);
      }
    }

    // Soft delete for removed items
    for (DocumentoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }

  public AnexoRespDTO toRespDto(DocumentoEntity d){
    AnexoRespDTO ar = new AnexoRespDTO();
    ar.setId(d.getId());
    ar.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
    ar.setTipoDocumentoDesc(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getNome() : null);
    ar.setDocumento(d.getReferenciaName());
    return ar;
  }

  public List<AnexoRespDTO> toAnexoRespDTOList(java.util.List<DocumentoEntity> documentos){
    return documentos.stream().map(this::toRespDto).toList();
  }


}
