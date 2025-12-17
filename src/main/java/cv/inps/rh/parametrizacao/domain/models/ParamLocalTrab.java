package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class ParamLocalTrab {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String nome;
  private Geografia pais;
  private Geografia ilha;
  private Long ups;
  private final Estado estado;

  private ParamLocalTrab(
      Long id,
      IdentificadorUnico uuid,
      String nome,
      Geografia pais,
      Geografia ilha,
      Long ups,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.nome = nome;
    this.pais = pais;
    this.ilha = ilha;
    this.ups = ups;
    this.estado = estado;
  }

  public static ParamLocalTrab create(
      String nome,
      Geografia pais,
      Geografia ilha,
      Long ups,
      Estado estado
  ) {
    return new ParamLocalTrab(
        null,
        IdentificadorUnico.create(),
        nome,
        pais,
        ilha,
        ups,
        estado
    );
  }

  public static ParamLocalTrab rebuild(
      Long id,
      java.util.UUID uuid,
      String nome,
      Geografia pais,
      Geografia ilha,
      Long ups,
      Estado estado
  ) {
    return new ParamLocalTrab(
        id,
        IdentificadorUnico.from(uuid),
        nome,
        pais,
        ilha,
        ups,
        estado
    );
  }

  public static ParamLocalTrab rebuild(
      Long id
  ) {
    return new ParamLocalTrab(
        id,
        null,
        null,
        null,
        null,
        null,
        null
    );
  }


  public void update(
      String nome,
      Geografia pais,
      Geografia ilha,
      Long ups
  ) {
    if (nome != null) this.nome = nome;
    if (pais != null) this.pais = pais;
    if (ilha != null) this.ilha = ilha;
    if (ups != null) this.ups = ups;
  }
}
