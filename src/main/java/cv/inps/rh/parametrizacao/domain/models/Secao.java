package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class Secao {

  private Long id;
  private IdentificadorUnico uuid;
  private String nome;
  private Long instId;
  private Estado estado;

  private Secao(Long id, IdentificadorUnico uuid, String nome, Long instId, Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.nome = nome;
    this.instId = instId;
    this.estado = estado;
  }

  public static Secao create(String nome, Long instId, Estado estado) {
    return new Secao(
        null,
        IdentificadorUnico.create(),
        nome,
        instId,
        estado
    );
  }

  public static Secao rebuild(Long id, java.util.UUID uuid, String nome, Long instId, Estado estado) {
    return new Secao(
        id,
        IdentificadorUnico.from(uuid),
        nome,
        instId,
        estado
    );
  }

  public void update(String nome, Long instId) {
    if (nome != null) this.nome = nome;
    if (instId != null) this.instId = instId;
  }
}
