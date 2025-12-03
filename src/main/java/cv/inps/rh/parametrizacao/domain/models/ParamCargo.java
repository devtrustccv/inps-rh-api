package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ParamCargo {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String nome;
  private ParamCarreira carreira;
  private String dirigente;
  private final Estado estado;

  private ParamCargo(Long id, IdentificadorUnico uuid, String nome, ParamCarreira carreira, String dirigente, Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.nome = nome;
    this.carreira = carreira;
    this.dirigente = dirigente;
    this.estado = estado;
  }

  public static ParamCargo create(String nome, ParamCarreira carreira, String dirigente, Estado estado) {
    return new ParamCargo(null, IdentificadorUnico.create(), nome, carreira, dirigente, estado);
  }

  public static ParamCargo rebuild(Long id, UUID uuid, String nome, ParamCarreira carreira, String dirigente, Estado estado) {
    return new ParamCargo(id, IdentificadorUnico.from(uuid), nome, carreira, dirigente, estado);
  }

  public static ParamCargo rebuild(Long id) {
    return new ParamCargo(id, null, null, null, null, null);
  }

  public void update(String nome, ParamCarreira carreira, String dirigente) {
    this.nome = nome;
    this.carreira = carreira;
    this.dirigente = dirigente;
  }
}
