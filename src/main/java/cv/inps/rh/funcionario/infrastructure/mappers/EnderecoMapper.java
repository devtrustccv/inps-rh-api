package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
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
    e.setMorada(dto.getMorada());
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
    e.setMorada(dto.getMorada());
    return e;
  }


  private <T> T ref(Class<T> type, Long id) {
    return id == null ? null : em.getReference(type, id);
  }

}
