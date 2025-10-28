package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ParamCarreira {

  private Long id;
  private IdentificadorUnico uuid;
  private String nome;
  private String codigo;
  private Estado estado;

  private ParamCarreira(Long id, IdentificadorUnico uuid, String nome, String codigo, Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.nome = nome;
    this.codigo = codigo;
    this.estado = estado;
  }

  public static ParamCarreira create(String nome, String codigo) {
    return new ParamCarreira(null, IdentificadorUnico.create(), nome, codigo, Estado.A);
  }

  public static ParamCarreira rebuild(Long id, UUID uuid, String nome, String codigo, Estado estado) {
    return new ParamCarreira(id, IdentificadorUnico.from(uuid), nome, codigo, estado);
  }

  public static ParamCarreira rebuild(Long id) {
    return new ParamCarreira(id, null, null,null,null);
  }


  public void update(String nome, String codigo) {
    this.nome = nome;
    this.codigo = codigo;
  }
}
