package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.util.ValidationUtil;
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
    validarDadosBancarios(dto);
    DadosBancariosEntity entity = new DadosBancariosEntity();
    entity.setRhbId(ValidationUtil.ref(entityManager, BancoEntity.class, dto.getEntidadeBancariaId()));
    entity.setNumConta(dto.getNumConta());
    entity.setNib(ValidationUtil.sanitizeNib(dto.getNib()));
    entity.setDataInicio(dto.getDataInicio());
    entity.setDataFim(dto.getDataFim());
    entity.setFunId(funcionario);
    entity.setEstado(estado);
    return entity;
  }

  private void validarDadosBancarios(DadosBancariosReqDTO dto) {
    if (dto.getEntidadeBancariaId() == null) {
      throw IgrpResponseStatusException.badRequest("Entidade Bancária é obrigatória.");
    }
    if (dto.getDataInicio() != null && dto.getDataFim() != null
        && dto.getDataInicio().isAfter(dto.getDataFim())) {
      throw IgrpResponseStatusException.badRequest(
          "A Data de Início não pode ser posterior à Data de Fim.");
    }
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
        validarDadosBancarios(dto);
        Long bancoAtualId = found.getRhbId() != null ? found.getRhbId().getId() : null;
        String nibSanitizado = ValidationUtil.sanitizeNib(dto.getNib());
        boolean mudou = !Objects.equals(dto.getEntidadeBancariaId(), bancoAtualId)
            || !Objects.equals(nibSanitizado, found.getNib())
            || !Objects.equals(dto.getNumConta(), found.getNumConta());

        found.setRhbId(ValidationUtil.ref(entityManager, BancoEntity.class, dto.getEntidadeBancariaId()));
        found.setNumConta(dto.getNumConta());
        found.setNib(nibSanitizado);
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataFim());

        if (mudou) found.setEstado(Estado.P);
      } else {
        DadosBancariosEntity novo = toEntity(dto, Estado.P, funcionario);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (DadosBancariosEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
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
      br.setNib(b.getNib());
      br.setDataInicio(b.getDataInicio());
      br.setDataFim(b.getDataFim());
      br.setEstado(b.getEstado() != null ? b.getEstado().getCode() : null);
      br.setEstadoDesc(b.getEstado() != null ? b.getEstado().getDescription() : null);
      return br;
    }).toList();
  }


}
