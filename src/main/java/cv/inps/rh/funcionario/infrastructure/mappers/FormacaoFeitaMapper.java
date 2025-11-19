package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalRespDTO;
import cv.inps.rh.funcionario.domain.models.FormacaoFeita;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.FormacaoFeitaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormacaoFeitaMapper {


  private final GeografiaMapper geografiaMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public FormacaoFeita toDomain(FormacaoFeitaEntity entity) {
    if (entity == null) return null;

    Geografia paisRef = entity.getPaisId() != null
        ? geografiaMapper.toDomain(entity.getPaisId())
        : null;

    return FormacaoFeita.rebuild(
        entity.getId(),
        entity.getUuid(),
        paisRef,
        entity.getEstabelecimento(),
        entity.getRhtpfor(), // grauAcademico
        entity.getCurso(),
        entity.getNivel(),
        entity.getEstado()
    );
  }

  // Domain -> Entity usando referência do EntityManager
  public FormacaoFeitaEntity toEntity(FormacaoFeita domain) {
    if (domain == null) return null;

    FormacaoFeitaEntity entity = new FormacaoFeitaEntity();
    if(domain.getId() !=null && domain.getId()>0)
     entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());

    // Apenas referência de Geografia
    if (domain.getPais() != null) {
      entity.setPaisId(entityManager.getReference(
          GeografiaEntity.class,
          domain.getPais().getId()
      ));
    }

    entity.setEstabelecimento(domain.getEstabelecimento());
    entity.setRhtpfor(domain.getTipoFormacao());
    entity.setCurso(domain.getCurso());
    entity.setNivel(domain.getNivel());
    entity.setEstado(domain.getEstado());

    return entity;

  }

  public FormacaoFeita toDomain(FormacaoProfissionalReqDTO dto) {
    if (dto == null) return null;

    return FormacaoFeita.create(
        dto.getId(),
        dto.getPaisId() != null ? geografiaMapper.toDomain(dto.getPaisId()) : null,
        dto.getEstabelecimento(),
        dto.getTipoFormacao(),   // substitui rhtpfor
        dto.getDesignacao(),     // curso
        dto.getNivel()
    );
  }

  public List<FormacaoFeita> toFormacoesFeitasDomain(List<FormacaoProfissionalReqDTO> dtos) {
    if (dtos == null) return null;

    return dtos.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }


  public FormacaoProfissionalRespDTO toResponseDTO(FormacaoFeita domain) {
    if(domain == null) return null;

    FormacaoProfissionalRespDTO dto = new FormacaoProfissionalRespDTO();
    dto.setId(domain.getId());
    dto.setUuid(domain.getUuid().getValor().toString());
    dto.setPaisId(domain.getPais() != null ? domain.getPais().getId() : null);
    dto.setPaisDesc(domain.getPais() != null ? domain.getPais().getNome() : null);
    dto.setEstabelecimento(domain.getEstabelecimento());
    dto.setTipoFormacao(domain.getTipoFormacao());
    dto.setDesignacao(domain.getCurso());
    dto.setNivel(domain.getNivel());
    dto.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);

    return dto;
  }


  public List<FormacaoProfissionalRespDTO> toResponseDTOList(List<FormacaoFeita> domains) {
    if(domains == null) return null;
    return domains.stream()
        .map(this::toResponseDTO)
        .collect(Collectors.toList());
  }

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
