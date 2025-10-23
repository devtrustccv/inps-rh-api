package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.shared.application.constants.Estado;
import org.springframework.stereotype.Component;

@Component
public class EstadoMapper {

  public Estado fromString(String estado) {
    if (estado == null || estado.isBlank()) return null;
    try {
      return Estado.valueOf(estado);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Estado value: " + estado, e);
    }
  }
}
