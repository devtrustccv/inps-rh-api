package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TiposRelacionamentoMapper {

  private final EntityManager entityManager;


  private <T> T getReferenceIfNotNull(Class<T> clazz, Long id) {
    return ValidationUtil.ref(entityManager, clazz, id);
  }


}
