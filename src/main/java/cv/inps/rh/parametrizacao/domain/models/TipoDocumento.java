package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;

import java.util.UUID;

public record TipoDocumento(Long id, IdentificadorUnico uuid, String referencia, String codigo,
                            String nome, Estado estado) {

  /**
   * Factory para criar um novo TipoDocumento
   */
  public static TipoDocumento create(
      String referencia,
      String codigo,
      String nome
  ) {

    return new TipoDocumento(
        null,
        IdentificadorUnico.create(),
        referencia,
        codigo,
        nome,
        Estado.A // estado padrão ativo
    );
  }

  public static TipoDocumento rebuild(
      Long id,
      UUID uuid,
      String referencia,
      String codigo,
      String nome,
      Estado estado
  ) {
    return new TipoDocumento(
        id,
        IdentificadorUnico.from(uuid),
        referencia,
        codigo,
        nome,
        estado
    );
  }

  public static TipoDocumento rebuild(
      Long id
  ) {
    return new TipoDocumento(
        id,
        null,
        null,
        null,
        null,
        null
    );
  }
}
