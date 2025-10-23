package cv.inps.rh.shared.domain.models;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Objects;
import java.util.UUID;

public class IdentificadorUnico {

  /**
   * Value Object genérico que representa um identificador único no domínio.
   * Imutável e comparado por valor.
   */

    private final UUID valor;

    private IdentificadorUnico(UUID valor) {
      if (valor == null) {
        throw IgrpResponseStatusException.badRequest("O identificador não pode ser nulo.");
      }
      this.valor = valor;
    }

    /** Cria um novo identificador único ordenado pelo tempo. */
    public static IdentificadorUnico novo() {
      return new IdentificadorUnico(UuidCreator.getTimeOrdered());
    }

    /** Reconstrói a partir de um UUID existente. */
    public static IdentificadorUnico de(UUID uuid) {
      return new IdentificadorUnico(uuid);
    }

    /** Reconstrói a partir de String. */
    public static IdentificadorUnico de(String uuidString) {
      if (uuidString == null || uuidString.isBlank()) {
        throw IgrpResponseStatusException.badRequest("UUID string não pode ser nula ou vazia.");
      }
      try {
        return new IdentificadorUnico(UUID.fromString(uuidString));
      } catch (IllegalArgumentException e) {
        throw IgrpResponseStatusException.badRequest("String fornecida não é um UUID válido. " + e.getMessage());
      }
    }

    /** Retorna o valor bruto do UUID. */
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
