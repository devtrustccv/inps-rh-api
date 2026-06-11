package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HabilitacaoLiterariaMapper {

  private final EntityManager entityManager;


  public HabilitacaoLiterariaEntity toEntity(HabilitacaoLiterariaReqDTO dto, Estado estado, FuncionarioEntity fun) {
    if (dto == null) {
      return null;
    }
    HabilitacaoLiterariaEntity e = new HabilitacaoLiterariaEntity();

    // ID (caso update)
    if (dto.getId() != null && dto.getId() > 0)
      e.setId(dto.getId());

    e.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));
    e.setEstabelecimento(ValidationUtil.trimToNull(dto.getEstabelecimento()));
    e.setArea(ValidationUtil.trimToNull(dto.getArea()));
    e.setNomeCurso(ValidationUtil.trimToNull(dto.getCurso()));
    e.setNivel(ValidationUtil.trimToNull(dto.getGrauAcademico()));
    e.setDataInicio(dto.getDataInicio());
    e.setDataFim(dto.getDataTermino());
    e.setConcluido(dto.getConcluido());
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setFunId(fun);
    e.setEstado(estado);


    return e;
  }

  public java.util.List<HabilitacaoLiterariaEntity> syncHabilitacoes(List<HabilitacaoLiterariaEntity> existingList,
                               java.util.List<HabilitacaoLiterariaReqDTO> newList, FuncionarioEntity fun, Estado estadoParaNovos) {
    if (newList == null) return existingList;
    for (HabilitacaoLiterariaReqDTO dto : newList) {
      HabilitacaoLiterariaEntity found = null;
      if (dto.getId() != null) {
        for (HabilitacaoLiterariaEntity h : existingList) {
          if (java.util.Objects.equals(h.getId(), dto.getId())) { found = h; break; }
        }
      }
      if (found != null) {
        found.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));
        found.setEstabelecimento(ValidationUtil.trimToNull(dto.getEstabelecimento()));
        found.setArea(ValidationUtil.trimToNull(dto.getArea()));
        found.setNomeCurso(ValidationUtil.trimToNull(dto.getCurso()));
        found.setNivel(ValidationUtil.trimToNull(dto.getGrauAcademico()));
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataTermino());
        found.setConcluido(dto.getConcluido());
      } else {
        HabilitacaoLiterariaEntity novo = toEntity(dto, estadoParaNovos, fun);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (HabilitacaoLiterariaEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto ->
              java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
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
