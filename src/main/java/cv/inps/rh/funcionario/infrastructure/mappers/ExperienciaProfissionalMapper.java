package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalReqDTO;
import cv.inps.rh.funcionario.application.dto.ExperienciaProfissionalRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ExperienciaProfEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExperienciaProfissionalMapper {

  private final EntityManager entityManager;

  public ExperienciaProfEntity toEntity(ExperienciaProfissionalReqDTO dto, Estado estado,
  FuncionarioEntity fun) {
    if (dto == null) {
      return null;
    }

    ExperienciaProfEntity e = new ExperienciaProfEntity();

    if(dto.getId() !=null && dto.getId()>0)
      e.setId(dto.getId());

    e.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));

    e.setEmpresa(ValidationUtil.trimToNull(dto.getEmpresa()));
    e.setCargo(ValidationUtil.trimToNull(dto.getCargo()));
    e.setDataInicio(dto.getDataEntrada());
    e.setDataFim(dto.getDataSaida());
    e.setObservacao(ValidationUtil.trimToNull(dto.getObservacoes()));
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setFunId(fun);
    e.setEstado(estado);

    return e;
  }

  public java.util.List<ExperienciaProfEntity> syncExperiencias(java.util.List<ExperienciaProfEntity> existingList,
                               java.util.List<ExperienciaProfissionalReqDTO> newList, FuncionarioEntity fun, Estado estadoParaNovos) {
    if (newList == null) return existingList;
    for (ExperienciaProfissionalReqDTO dto : newList) {
      ExperienciaProfEntity found = null;
      if (dto.getId() != null) {
        for (ExperienciaProfEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        found.setPaisId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dto.getPaisId()));
        found.setEmpresa(ValidationUtil.trimToNull(dto.getEmpresa()));
        found.setCargo(ValidationUtil.trimToNull(dto.getCargo()));
        found.setDataInicio(dto.getDataEntrada());
        found.setDataFim(dto.getDataSaida());
        found.setObservacao(ValidationUtil.trimToNull(dto.getObservacoes()));
      } else {
        ExperienciaProfEntity novo = toEntity(dto, estadoParaNovos, fun);
        existingList.add(novo);
      }
    }

    // Soft delete
    for (ExperienciaProfEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public List<ExperienciaProfissionalRespDTO> toExperienciaProfissionalRespDTOList(List<ExperienciaProfEntity> experienciasProfissionais) {
    return experienciasProfissionais.stream().map(e -> {
      ExperienciaProfissionalRespDTO er = new ExperienciaProfissionalRespDTO();
      er.setId(e.getId());
      er.setPaisId(e.getPaisId() != null ? e.getPaisId().getId() : null);
      er.setPaisDesc(e.getPaisId() != null ? e.getPaisId().getNome() : null);
      er.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);
      er.setEmpresa(e.getEmpresa());
      er.setCargo(e.getCargo());
      er.setDataEntrada(e.getDataInicio());
      er.setDataSaida(e.getDataFim());
      er.setObservacoes(e.getObservacao());
      er.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
      return er;
    }).toList();
  }
}
