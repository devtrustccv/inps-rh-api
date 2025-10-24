package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.funcionario.domain.models.DadosBancarios;
import cv.inps.rh.shared.domain.models.Entidade;
import cv.inps.rh.shared.infrastructure.mappers.EntidadeMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EntidadeEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DadosBancariosMapper {

  private final EntityManager entityManager;

  private final EntidadeMapper entidadeMapper;


  // Entity -> Domain
  public DadosBancarios toDomain(DadosBancariosEntity entity) {
    if (entity == null) return null;

    Entidade entidadeRef = entity.getEntId() != null
        ? Entidade.rebuild(entity.getEntId().getId(), entity.getEntId().getNome())
        : null;

    return DadosBancarios.rebuild(
        entity.getId(),
        entity.getUuid(),
        entidadeRef,
        entity.getNumConta(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado(),
        entity.getObs()
    );
  }

  // Domain -> Entity (apenas referência do EntityManager)
  public DadosBancariosEntity toEntity(DadosBancarios domain) {
    if (domain == null) return null;

    DadosBancariosEntity entity = new DadosBancariosEntity();
    if (domain.getId() != null && domain.getId() > 0) {
      entity.setId(domain.getId());
    }
    entity.setUuid(domain.getUuid().getValor());

    if (domain.getEntidade() != null) {
      entity.setEntId(entityManager.getReference(
          EntidadeEntity.class,
          domain.getEntidade().getId()
      ));
    }

    entity.setNumConta(domain.getNumConta());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setObs(domain.getObservacoes());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  public DadosBancarios toDomain(DadosBancariosReqDTO dto) {
    if (dto == null) return null;

    Entidade entidade = dto.getEntidadeBancariaId() != null
        ? entidadeMapper.toDomain(dto.getEntidadeBancariaId())
        : null;

    return DadosBancarios.create(
        dto.getId(),
        entidade,
        dto.getNumConta(),
        dto.getDataInicio(),
        dto.getDataFim(),
        null // observacoes, se houver, pode ser adicionado
    );
  }

  public List<DadosBancarios> toDomainList(java.util.List<DadosBancariosReqDTO> dtos) {
    if (dtos == null) return null;
    return dtos.stream()
        .map(this::toDomain)
        .toList();
  }

  public DadosBancariosRespDTO toResponseDTO(DadosBancarios domain) {
    if (domain == null) return null;

    DadosBancariosRespDTO dto = new DadosBancariosRespDTO();
    dto.setId(domain.getId());
    dto.setEntidadeBancariaId(domain.getEntidade() != null ? domain.getEntidade().getId() : null);
    dto.setEntidadeBancariaDesc(domain.getEntidade() != null ? domain.getEntidade().getNome() : null);
    dto.setNumConta(domain.getNumConta());
    dto.setDataInicio(domain.getDataInicio());
    dto.setDataFim(domain.getDataFim());

    return dto;
  }

  public List<DadosBancariosRespDTO> toResponseDTOList(List<DadosBancarios> domains) {
    if (domains == null) return null;
    return domains.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

}
