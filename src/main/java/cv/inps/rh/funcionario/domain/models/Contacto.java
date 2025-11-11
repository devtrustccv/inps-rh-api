package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Contacto {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String tipoContacto;
  private String contacto;
  private Estado estado;

  private Contacto(Long id, IdentificadorUnico uuid, String tipoContacto, String contacto, Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.tipoContacto = tipoContacto;
    this.contacto = contacto;
    this.estado = estado;
  }

  public static Contacto create(Long id, String tipoContacto, String contacto) {
    var updatedId = id != null && id > 0 ? id : null;
    return new Contacto(
        updatedId,
        IdentificadorUnico.create(),
        tipoContacto,
        contacto,
        Estado.P
    );
  }

  public static Contacto rebuild(Long id, UUID uuid, String tipoContacto, String contacto, Estado estado) {
    return new Contacto(
        id,
        IdentificadorUnico.from(uuid),
        tipoContacto,
        contacto,
        estado
    );
  }

  public void update(String tipoContacto, String contacto) {
    if(tipoContacto != null && !tipoContacto.isBlank()) {
      this.tipoContacto = tipoContacto;
    }
    if(contacto != null && !contacto.isBlank()) {
      this.contacto = contacto;
    }
  }

  public void eliminar() {
    this.estado = Estado.E;
  }

}
