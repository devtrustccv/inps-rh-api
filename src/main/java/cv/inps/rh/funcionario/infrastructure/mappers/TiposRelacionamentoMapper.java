package cv.inps.rh.funcionario.infrastructure.mappers;

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
