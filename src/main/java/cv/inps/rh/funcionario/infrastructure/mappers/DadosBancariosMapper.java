package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Banco;
import cv.inps.rh.shared.infrastructure.mappers.BancoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DadosBancariosMapper {

  private final EntityManager entityManager;

   private final BancoMapper bancoMapper;


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

  public java.util.List<DadosBancariosEntity> syncBancarios(java.util.List<DadosBancariosEntity> existingList,
                            java.util.List<DadosBancariosReqDTO> newList) {
    if (newList == null) return existingList;

    for (DadosBancariosReqDTO dto : newList) {
      DadosBancariosEntity found = null;
      if (dto.getId() != null) {
        for (DadosBancariosEntity b : existingList) {
          if (Objects.equals(b.getId(), dto.getId())) {
            found = b;
            break;
          }
        }
      }
      if (found != null) {
        if (dto.getEntidadeBancariaId() != null) {
          found.setRhbId(entityManager.getReference(BancoEntity.class, dto.getEntidadeBancariaId()));
        }
        found.setNumConta(dto.getNumConta());
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataFim());
      } else {
        DadosBancariosEntity novo = toEntity(dto, Estado.P);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (DadosBancariosEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


}
