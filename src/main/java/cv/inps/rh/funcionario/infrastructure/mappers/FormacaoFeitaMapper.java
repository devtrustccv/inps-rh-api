package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalRespDTO;
import cv.inps.rh.funcionario.domain.models.FormacaoFeita;
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

  public FormacaoFeitaEntity toEntity(FormacaoProfissionalReqDTO dto) {
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

    return e;
  }


}
