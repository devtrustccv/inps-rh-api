package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HabilitacaoLiterariaMapper {

  private final EntityManager entityManager;


  public HabilitacaoLiterariaEntity toEntity(HabilitacaoLiterariaReqDTO dto, Estado estado) {
    if (dto == null) {
      return null;
    }
    HabilitacaoLiterariaEntity e = new HabilitacaoLiterariaEntity();

    // ID (caso update)
    if (dto.getId() != null && dto.getId() > 0)
      e.setId(dto.getId());

    // Referência para país
    if (dto.getPaisId() != null) {
      e.setPaisId(entityManager.getReference(GeografiaEntity.class, dto.getPaisId()));
    }
    e.setEstabelecimento(dto.getEstabelecimento());
    e.setArea(dto.getArea());
    e.setNomeCurso(dto.getCurso());
    e.setNivel(dto.getGrauAcademico());
    e.setDataInicio(dto.getDataInicio());
    e.setDataFim(dto.getDataTermino());
    e.setConcluido(dto.getConcluido());
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(estado);


    return e;
  }

  public java.util.List<HabilitacaoLiterariaEntity> syncHabilitacoes(List<HabilitacaoLiterariaEntity> existingList,
                               java.util.List<HabilitacaoLiterariaReqDTO> newList) {
    if (newList == null) return existingList;
    for (HabilitacaoLiterariaReqDTO dto : newList) {
      HabilitacaoLiterariaEntity found = null;
      if (dto.getId() != null) {
        for (HabilitacaoLiterariaEntity h : existingList) {
          if (java.util.Objects.equals(h.getId(), dto.getId())) { found = h; break; }
        }
      }
      if (found != null) {
        if (dto.getPaisId() != null) {
          found.setPaisId(entityManager.getReference(GeografiaEntity.class, dto.getPaisId()));
        }
        found.setEstabelecimento(dto.getEstabelecimento());
        found.setArea(dto.getArea());
        found.setNomeCurso(dto.getCurso());
        found.setNivel(dto.getGrauAcademico());
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataTermino());
        found.setConcluido(dto.getConcluido());
      } else {
        HabilitacaoLiterariaEntity novo = toEntity(dto, Estado.P);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (HabilitacaoLiterariaEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


    public List<HabilitacaoLiterariaRespDTO> toHabilitacaoLiterariaRespDTOList(List<HabilitacaoLiterariaEntity> habilitacoesLiterarias) {
      return habilitacoesLiterarias.stream().map(h -> {
        HabilitacaoLiterariaRespDTO hr = new HabilitacaoLiterariaRespDTO();
        hr.setId(h.getId());
        hr.setPaisId(h.getPaisId() != null ? h.getPaisId().getId() != null ? h.getPaisId().getId().intValue() : null : null);
        hr.setPaisDesc(h.getPaisId() != null ? h.getPaisId().getNome() : null);
        hr.setEstabelecimento(h.getEstabelecimento());
        hr.setArea(h.getArea());
        hr.setCurso(h.getNomeCurso());
        hr.setGrauAcademico(h.getNivel());
        hr.setDataInicio(h.getDataInicio());
        hr.setDataTermino(h.getDataFim());
        hr.setConcluido(h.getConcluido());
        return hr;
      }).toList();
    }
}
