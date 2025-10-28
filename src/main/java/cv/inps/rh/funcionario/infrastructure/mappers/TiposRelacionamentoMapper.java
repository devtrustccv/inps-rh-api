package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TiposRelacionamentoMapper {

  private final EntityManager entityManager;


  public TiposRelacionamentoEntity toEntity(cv.inps.rh.funcionario.domain.models.TiposRelacionamento domain) {
    return  null ;
  }

  public cv.inps.rh.funcionario.domain.models.TiposRelacionamento toDomain(TiposRelacionamentoEntity entity) {
    return  null;
  }

}
