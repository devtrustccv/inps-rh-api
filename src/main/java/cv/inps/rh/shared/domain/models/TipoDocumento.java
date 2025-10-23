package cv.inps.rh.shared.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TipoDocumento {

  private final Long id;
  private final IdentificadorUnico uuid;
  private final String referencia;
  private final String codigo;
  private final String nome;
  private final Estado estado;

  private TipoDocumento(
      Long id,
      IdentificadorUnico uuid,
      String referencia,
      String codigo,
      String nome,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.referencia = referencia;
    this.codigo = codigo;
    this.nome = nome;
    this.estado = estado;
  }

  /** Factory para criar um novo TipoDocumento */
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
}
