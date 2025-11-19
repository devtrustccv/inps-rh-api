package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.funcionario.domain.models.DadosBancarios;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Banco;
import cv.inps.rh.shared.infrastructure.mappers.BancoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DadosBancariosMapper {

  private final EntityManager entityManager;

   private final BancoMapper bancoMapper;

  // Entity -> Domain
  public DadosBancarios toDomain(DadosBancariosEntity entity) {
    if (entity == null) return null;

    Banco banco = entity.getRhbId() != null
        ? bancoMapper.toDomain(entity.getRhbId())
        : null;

    return DadosBancarios.rebuild(
        entity.getId(),
        entity.getUuid(),
        banco,
        entity.getNumConta(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado(),
        entity.getObs()
    );
  }

  public DadosBancariosEntity toEntity(DadosBancarios domain) {
    if (domain == null) return null;

    DadosBancariosEntity entity = new DadosBancariosEntity();
    if (domain.getId() != null && domain.getId() > 0) {
      entity.setId(domain.getId());
    }
    entity.setUuid(domain.getUuid().getValor());

    if (domain.getBanco() != null) {
      entity.setRhbId(entityManager.getReference(
          BancoEntity.class,
          domain.getBanco().getId()
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

    Banco banco = dto.getEntidadeBancariaId() != null
        ? bancoMapper.toDomain(dto.getEntidadeBancariaId())
        : null;

    return DadosBancarios.create(
        dto.getId(),
        banco,
        dto.getNumConta(),
        dto.getDataInicio(),
        dto.getDataFim(),
        null // observacoes, se houver, pode ser adicionado
    );
  }

  public List<DadosBancarios> toDadosBancariosDomain(java.util.List<DadosBancariosReqDTO> dtos) {
    if (dtos == null) return null;
    return dtos.stream()
        .map(this::toDomain)
        .toList();
  }

  public DadosBancariosRespDTO toResponseDTO(DadosBancarios domain) {
    if (domain == null) return null;

    DadosBancariosRespDTO dto = new DadosBancariosRespDTO();
    dto.setId(domain.getId());
    dto.setEntidadeBancariaId(domain.getBanco() != null ? domain.getBanco().getId() : null);
    dto.setEntidadeBancariaDesc(domain.getBanco() != null ? domain.getBanco().getNomeBanco() : null);
    dto.setNumConta(domain.getNumConta());
    dto.setDataInicio(domain.getDataInicio());
    dto.setDataFim(domain.getDataFim());

    return dto;
  }

  public List<DadosBancariosRespDTO> toResponseDTOList(List<DadosBancarios> domains) {
    if (domains == null) return null;
    return domains.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  public DadosBancariosEntity toEntity(DadosBancariosReqDTO dto, Estado estado) {
    if (dto == null) return null;
    DadosBancariosEntity entity = new DadosBancariosEntity();
    if (dto.getEntidadeBancariaId() != null) {
      entity.setRhbId(entityManager.getReference(BancoEntity.class, dto.getEntidadeBancariaId()));
    }
    entity.setNumConta(dto.getNumConta());
    entity.setDataInicio(dto.getDataInicio());
    entity.setDataFim(dto.getDataFim());
    entity.setEstado(estado);
    return entity;
  }


}
