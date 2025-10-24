package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalRespDTO;
import cv.inps.rh.funcionario.domain.models.ExperienciaProfissional;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ExperienciaProfEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExperienciaProfissionalMapper {

  private final GeografiaMapper geografiaMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public ExperienciaProfissional toDomain(ExperienciaProfEntity entity) {
    if (entity == null) return null;

    Geografia paisRef = entity.getPaisId() != null
        ? geografiaMapper.toDomain(entity.getPaisId())
        : null;


    return ExperienciaProfissional.rebuild(
        entity.getId(),
        entity.getUuid(),
        paisRef,
        entity.getEmpresa(),
        entity.getCargo(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getObservacao(),
        entity.getEstado()
    );
  }

  // Domain -> Entity (usando referência do EntityManager)
  public ExperienciaProfEntity toEntity(ExperienciaProfissional domain) {
    if (domain == null) return null;

    ExperienciaProfEntity entity = new ExperienciaProfEntity();
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

    entity.setEmpresa(domain.getEmpresa());
    entity.setCargo(domain.getCargo());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim() != null ? domain.getDataFim() : null);
    entity.setObservacao(domain.getObservacao());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  // DTO -> Domain
  public ExperienciaProfissional toDomain(ExperienciaProfissionalReqDTO dto) {
    if (dto == null) return null;

    Geografia pais = dto.getPaisId() != null ? geografiaMapper.toDomain(dto.getPaisId()) : null;

    return ExperienciaProfissional.create(
        dto.getId(),
        pais,
        dto.getEmpresa(),
        dto.getCargo(),
        dto.getDataEntrada(),
        dto.getDataSaida(),
        dto.getObservacoes()
    );
  }

  public List<ExperienciaProfissional> toExperienciasProfissionaisDomain(List<ExperienciaProfissionalReqDTO> dtos) {
    if (dtos == null) return null;
    return dtos.stream()
        .map(this::toDomain)
        .toList();
  }

  public ExperienciaProfissionalRespDTO toResponseDTO(ExperienciaProfissional domain) {
    if (domain == null) return null;

    ExperienciaProfissionalRespDTO dto = new ExperienciaProfissionalRespDTO();
    dto.setId(domain.getId());
    dto.setUuid(domain.getUuid() != null ? domain.getUuid().getValor().toString() : null);
    dto.setPaisId(domain.getPais() != null ? domain.getPais().getId() : null);
    dto.setPaisDesc(domain.getPais() != null ? domain.getPais().getNome() : null);
    dto.setEmpresa(domain.getEmpresa());
    dto.setCargo(domain.getCargo());
    dto.setDataEntrada(domain.getDataInicio());
    dto.setDataSaida(domain.getDataFim());
    dto.setObservacoes(domain.getObservacao());
    dto.setEstado(domain.getEstado() != null ? domain.getEstado().getDescription() : null);

    return dto;
  }

  public List<ExperienciaProfissionalRespDTO> toResponseDTOList(List<ExperienciaProfissional> dominios) {
    if (dominios == null) return null;
    return dominios.stream()
        .map(this::toResponseDTO)
        .toList();
  }


}
