package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnderecoMapper {

  private final EntityManager em;

  public EnderecoEntity toEntity(EnderecoReqDTO dto, Estado estado) {
    if (dto == null) {
      return null;
    }
    EnderecoEntity e = new EnderecoEntity();
    e.setPaisId(ref(GeografiaEntity.class, dto.getPaisId()));
    e.setIlhaId(ref(GeografiaEntity.class, dto.getIlhaId()));
    e.setConcelhoId(ref(GeografiaEntity.class, dto.getConcelhoId()));
    e.setFreguesiaId(ref(GeografiaEntity.class, dto.getFreguesiaId()));
    e.setZonaId(ref(GeografiaEntity.class, dto.getZonaId()));
    e.setMorada(ValidationUtil.trimToNull(dto.getMorada()));
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(estado);
    return e;
  }

  public EnderecoEntity toUpdateEntity(EnderecoEntity e, EnderecoReqDTO dto) {
    if (dto == null) {
      return null;
    }
    e.setPaisId(ref(GeografiaEntity.class, dto.getPaisId()));
    e.setIlhaId(ref(GeografiaEntity.class, dto.getIlhaId()));
    e.setConcelhoId(ref(GeografiaEntity.class, dto.getConcelhoId()));
    e.setFreguesiaId(ref(GeografiaEntity.class, dto.getFreguesiaId()));
    e.setZonaId(ref(GeografiaEntity.class, dto.getZonaId()));
    e.setMorada(ValidationUtil.trimToNull(dto.getMorada()));
    return e;
  }

  public EnderecoRespDTO respDTO(EnderecoEntity entity){
    if (entity == null) {return null;}
    EnderecoRespDTO er = new EnderecoRespDTO();

    er.setId(entity.getId());
    if (entity.getPaisId() != null) {
      er.setPaisId(entity.getPaisId().getId() != null ? entity.getPaisId().getId() : null);
      er.setPaisDesc(entity.getPaisId().getNome());
    }
    if (entity.getIlhaId() != null) {
      er.setIlhaId(entity.getIlhaId().getId() != null ? entity.getIlhaId().getId() : null);
      er.setIlhaDesc(entity.getIlhaId().getNome());
    }
    if (entity.getConcelhoId() != null) {
      er.setConcelhoId(entity.getConcelhoId().getId() != null ? entity.getConcelhoId().getId() : null);
      er.setConcelhoDesc(entity.getConcelhoId().getNome());
    }
    if (entity.getFreguesiaId() != null) {
      er.setFreguesiaId(entity.getFreguesiaId().getId() != null ? entity.getFreguesiaId().getId() : null);
      er.setFreguesiaDesc(entity.getFreguesiaId().getNome());
    }
    if (entity.getZonaId() != null) {
      er.setZonaId(entity.getZonaId().getId() != null ? entity.getZonaId().getId(): null);
      er.setZonaDesc(entity.getZonaId().getNome());
    }
    er.setMorada(entity.getMorada());
    er.setEstado(entity.getEstado() != null ? entity.getEstado().getCode() : null);
    er.setEstadoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);
    er.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    return er;
  }


  private <T> T ref(Class<T> type, Long id) {
    return ValidationUtil.ref(em, type, id);
  }

}
