package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.EscalaoDTO;
import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.parametrizacao.domain.models.ParamCategoria;
import cv.inps.rh.parametrizacao.domain.models.ParamEscalao;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCategoriaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ParamEscalaoMapper {

  private final EntityManager entityManager;
  private final ParamCarreiraMapper carreiraMapper;
  private final ParamCategoriaMapper categoriaMapper;


  public ParamEscalao toDomain(ParamEscalaoEntity entity) {
    if (entity == null) return null;

    ParamCarreira carreira = null;
    if (entity.getParamCarrId() != null) {
      carreira = carreiraMapper.toDomain(entity.getParamCarrId().getId());
    }

    ParamCategoria categoria = null;
    if (entity.getParamCategoriaId() != null) {
      categoria = categoriaMapper.toDomain(entity.getParamCategoriaId().getId());
    }

    return ParamEscalao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getCodigo(),
        carreira,
        categoria,
        entity.getNivelReferencia(),
        entity.getEscalao(),
        entity.getValor(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado()
    );
  }

  // Domain -> Entity
  public ParamEscalaoEntity toEntity(ParamEscalao domain) {
    if (domain == null) return null;

    ParamEscalaoEntity entity = new ParamEscalaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().valor() : null);
    entity.setCodigo(domain.getCodigo());
    entity.setNivelReferencia(domain.getNivelReferencia());
    entity.setEscalao(domain.getEscalao());
    entity.setValor(domain.getValor());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setEstado(domain.getEstado());

    if (domain.getParamCarreira() != null && domain.getParamCarreira().getId() != null) {
      entity.setParamCarrId(entityManager.getReference(ParamCarreiraEntity.class, domain.getParamCarreira().getId()));
    }

    if (domain.getParamCategoria() != null && domain.getParamCategoria().getId() != null) {
      entity.setParamCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, domain.getParamCategoria().getId()));
    }

    return entity;
  }

  // Referência mínima
  public ParamEscalao toDomain(Long id) {
    if (id == null || id < 0) return null;
    return ParamEscalao.rebuild(id);
  }

  public EscalaoDTO toParametrizacaoDto(ParamEscalao domain) {
    if (domain == null) return null;

    EscalaoDTO dto = new EscalaoDTO();
    String label = Stream.of(
            Optional.ofNullable(domain.getNivelReferencia()).map(Object::toString).orElse("").trim(),
            Optional.ofNullable(domain.getEscalao()).orElse("").trim()
        )
        .filter(s -> !s.isEmpty())
        .collect(Collectors.joining("/"));

    dto.setLabel(label);
    dto.setValue(domain.getId());
    dto.setValor(domain.getValor());
    return dto;
  }

}
