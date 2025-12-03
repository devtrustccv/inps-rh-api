package cv.inps.rh.funcionario.infrastructure.mappers;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoMapper {

  private final EntityManager entityManager;



}
