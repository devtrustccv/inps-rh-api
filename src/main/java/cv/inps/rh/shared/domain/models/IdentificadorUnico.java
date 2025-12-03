package cv.inps.rh.shared.domain.models;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.UUID;

/**
 * Generic Value Object representing a unique identifier in the domain.
 * Immutable and value-based equality.
 *
 * @param valor -- GETTER --
 *              Returns the raw UUID value.
 */

public record IdentificadorUnico(UUID valor) {

  public IdentificadorUnico {
    if (valor == null) {
      throw IgrpResponseStatusException.badRequest("O identificador não pode ser nulo.");
    }
  }

  /**
   * Creates a new time-ordered unique identifier.
   */
  public static IdentificadorUnico create() {
    return new IdentificadorUnico(UuidCreator.getTimeOrdered());
  }

  /**
   * Rebuilds from an existing UUID.
   */
  public static IdentificadorUnico from(UUID uuid) {
    return new IdentificadorUnico(uuid);
  }

  /**
   * Rebuilds from a String representation of a UUID.
   */
  public static IdentificadorUnico from(String uuidString) {
    if (uuidString == null || uuidString.isBlank()) {
      throw IgrpResponseStatusException.badRequest("UUID string não pode ser nula ou vazia.");
    }
    try {
      return new IdentificadorUnico(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest(
          "String fornecida não é um UUID válido. " + e.getMessage()
      );
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IdentificadorUnico(UUID valor1))) return false;
    return valor.equals(valor1);
  }
}
