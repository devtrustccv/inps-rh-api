package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.parametrizacao.infrastructure.mappers.*;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TiposRelacionamentoMapper {

  private final EntityManager entityManager;


  private <T> T getReferenceIfNotNull(Class<T> clazz, Object id) {
    if (id == null) return null;
    return entityManager.getReference(clazz, id);
  }


}
