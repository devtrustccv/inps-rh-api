package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.FormacaoProfissionalRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FormacaoFeitaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FormacaoFeitaMapper {

  private final EntityManager entityManager;


  public FormacaoFeitaEntity toEntity(FormacaoProfissionalReqDTO dto, Estado estado, FuncionarioEntity fun) {
    if (dto == null) {
      return null;
    }
    FormacaoFeitaEntity e = new FormacaoFeitaEntity();

    if(dto.getId() !=null && dto.getId()>0)
     e.setId(dto.getId());

    e.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));

    e.setEstabelecimento(ValidationUtil.trimToNull(dto.getEstabelecimento()));
    e.setRhtpfor(ValidationUtil.trimToNull(dto.getTipoFormacao()));
    e.setCurso(ValidationUtil.trimToNull(dto.getDesignacao()));
    e.setNivel(ValidationUtil.trimToNull(dto.getNivel()));
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setFunId(fun);
    e.setEstado(estado);

    return e;
  }

  public java.util.List<FormacaoFeitaEntity> syncFormacoes(java.util.List<FormacaoFeitaEntity> existingList,
                            java.util.List<FormacaoProfissionalReqDTO> newList, FuncionarioEntity fun, Estado estadoParaNovos) {
    if (newList == null) return existingList;
    for (FormacaoProfissionalReqDTO dto : newList) {
      FormacaoFeitaEntity found = null;
      if (dto.getId() != null) {
        for (FormacaoFeitaEntity f : existingList) {
          if (java.util.Objects.equals(f.getId(), dto.getId())) { found = f; break; }
        }
      }
      if (found != null) {
        found.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));
        found.setEstabelecimento(ValidationUtil.trimToNull(dto.getEstabelecimento()));
        found.setRhtpfor(ValidationUtil.trimToNull(dto.getTipoFormacao()));
        found.setCurso(ValidationUtil.trimToNull(dto.getDesignacao()));
        found.setNivel(ValidationUtil.trimToNull(dto.getNivel()));
      } else {
        FormacaoFeitaEntity novo = toEntity(dto, estadoParaNovos, fun);
        existingList.add(novo);
      }
    }
    // Soft delete
    for (FormacaoFeitaEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public List<FormacaoProfissionalRespDTO> toFormacaoFeitaRespDTOList(List<FormacaoFeitaEntity> formacoesFeitas) {
    return formacoesFeitas.stream().map(f -> {
      FormacaoProfissionalRespDTO fr = new FormacaoProfissionalRespDTO();
      fr.setId(f.getId());
      fr.setUuid(f.getUuid() != null ? f.getUuid().toString() : null);
      fr.setPaisId(f.getPaisId() != null ? f.getPaisId().getId() : null);
      fr.setPaisDesc(f.getPaisId() != null ? f.getPaisId().getNome() : null);
      fr.setEstabelecimento(f.getEstabelecimento());
      fr.setTipoFormacao(f.getRhtpfor());
      fr.setDesignacao(f.getCurso());
      fr.setNivel(f.getNivel());
      fr.setEstado(f.getEstado() != null ? f.getEstado().name() : null);
      return fr;
    }).toList();
  }
}
