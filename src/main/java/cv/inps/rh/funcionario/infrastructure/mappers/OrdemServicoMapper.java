package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoMapper {

  private final EntityManager entityManager;



}
