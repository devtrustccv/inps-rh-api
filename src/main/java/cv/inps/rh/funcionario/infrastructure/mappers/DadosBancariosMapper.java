package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.BancoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DadosBancariosMapper {

  private final EntityManager entityManager;

  public DadosBancariosEntity toEntity(DadosBancariosReqDTO dto, Estado estado,
                                       FuncionarioEntity funcionario) {
    if (dto == null) return null;
    DadosBancariosEntity entity = new DadosBancariosEntity();
    if (dto.getEntidadeBancariaId() != null) {
      entity.setRhbId(entityManager.getReference(BancoEntity.class, dto.getEntidadeBancariaId()));
    }
    entity.setNumConta(dto.getNumConta());
    entity.setDataInicio(dto.getDataInicio());
    entity.setDataFim(dto.getDataFim());
    entity.setFunId(funcionario);
    entity.setEstado(estado);
    return entity;
  }

  public List<DadosBancariosEntity> syncBancarios(List<DadosBancariosEntity> existingList,
                                                  List<DadosBancariosReqDTO> newList, FuncionarioEntity funcionario) {
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
        DadosBancariosEntity novo = toEntity(dto, Estado.P, funcionario);
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

  public List<DadosBancariosRespDTO> toDadosBancariosRespDTOList(List<DadosBancariosEntity> entities) {
    return entities.stream().map(b -> {
      DadosBancariosRespDTO br = new DadosBancariosRespDTO();
      br.setId(b.getId());
      br.setEntidadeBancariaId(b.getRhbId() != null ? b.getRhbId().getId() : null);
      br.setEntidadeBancariaDesc(b.getRhbId() != null ? b.getRhbId().getNmBanco() : null);
      br.setNumConta(b.getNumConta());
      br.setDataInicio(b.getDataInicio());
      br.setDataFim(b.getDataFim());
      return br;
    }).toList();
  }


}
