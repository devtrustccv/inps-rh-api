package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Endereco {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Geografia pais;
  private Geografia ilha;
  private Geografia concelho;
  private Geografia freguesia;
  private Geografia zona;
  private String morada;
  private Estado estado;

  private Endereco(Long id, IdentificadorUnico uuid, Geografia pais, Geografia ilha,
                   Geografia concelho, Geografia freguesia ,Geografia zona, String morada, Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.pais = pais;
    this.ilha = ilha;
    this.concelho = concelho;
    this.freguesia = freguesia;
    this.zona = zona;
    this.morada = morada;
    this.estado = estado;
  }

  public static Endereco create(Long id, Geografia pais, Geografia ilha ,Geografia concelho, Geografia freguesia, Geografia zona,
                                String morada) {

    var updatedId = id != null && id > 0 ? id : null;

    return new Endereco(
        null,
        IdentificadorUnico.create(),
        pais,
        ilha,
        concelho,
        freguesia,
        zona,
        morada,
        Estado.A
    );
  }

  public static Endereco rebuild(Long id, UUID uuid, Geografia pais, Geografia ilha,
                                 Geografia concelho,Geografia freguesia, Geografia zona, String morada, Estado estado) {
    return new Endereco(
        id,
        IdentificadorUnico.from(uuid),
        pais,
        ilha,
        concelho,
        freguesia,
        zona,
        morada,
        estado
    );
  }

  public void update(Geografia pais, Geografia ilha, Geografia concelho, Geografia zona, String morada) {
    if (pais != null) this.pais = pais;
    if (ilha != null) this.ilha = ilha;
    if (concelho != null) this.concelho = concelho;
    if (zona != null) this.zona = zona;
    if (morada != null && !morada.isBlank()) this.morada = morada;
  }

  public void eliminar() {
    this.estado = Estado.E;
  }

}
