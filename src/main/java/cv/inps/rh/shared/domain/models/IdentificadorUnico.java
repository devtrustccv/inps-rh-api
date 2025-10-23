package cv.inps.rh.shared.domain.models;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Objects;
import java.util.UUID;

/**
 * Generic Value Object representing a unique identifier in the domain.
 * Immutable and value-based equality.
 */

public class IdentificadorUnico {

  private final UUID valor;

  private IdentificadorUnico(UUID valor) {
    if (valor == null) {
      throw IgrpResponseStatusException.badRequest("O identificador não pode ser nulo.");
    }
    this.valor = valor;
  }

  /** Creates a new time-ordered unique identifier. */
  public static IdentificadorUnico create() {
    return new IdentificadorUnico(UuidCreator.getTimeOrdered());
  }

  /** Rebuilds from an existing UUID. */
  public static IdentificadorUnico from(UUID uuid) {
    return new IdentificadorUnico(uuid);
  }

  /** Rebuilds from a String representation of a UUID. */
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

  /** Returns the raw UUID value. */
  public UUID getValor() {
    return valor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IdentificadorUnico that)) return false;
    return valor.equals(that.valor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valor);
  }

  @Override
  public String toString() {
    return valor.toString();
  }
}
