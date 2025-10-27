package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class ParamCategoria {

  private Long id;
  private ParamCarreira paramCarreira;
  private IdentificadorUnico uuid;
  private String nome;
  private String codigo;
  private Estado estado;

  private ParamCategoria(Long id, ParamCarreira paramCarreira, IdentificadorUnico uuid, String nome, String codigo, Estado estado) {
    this.id = id;
    this.paramCarreira = paramCarreira;
    this.uuid = uuid;
    this.nome = nome;
    this.codigo = codigo;
    this.estado = estado;
  }

  public static ParamCategoria create(ParamCarreira paramCarreira, String nome, String codigo, Estado estado) {
    return new ParamCategoria(null, paramCarreira, IdentificadorUnico.create(), nome, codigo, estado);
  }

  public static ParamCategoria rebuild(Long id, ParamCarreira paramCarreira, java.util.UUID uuid, String nome, String codigo, Estado estado) {
    return new ParamCategoria(id, paramCarreira, IdentificadorUnico.from(uuid), nome, codigo, estado);
  }

  public void update(String nome, String codigo, Estado estado) {
    this.nome = nome;
    this.codigo = codigo;
    this.estado = estado;
  }
}
