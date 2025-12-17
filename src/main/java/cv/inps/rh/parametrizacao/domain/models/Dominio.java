package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Getter;

@Getter
public class Dominio {

  private final Long id;
  private String dominio;
  private String valor;
  private String descricao;
  private String referencia;
  private Estado estado;

  private Dominio(Long id, String dominio, String valor, String descricao, String referencia, Estado estado) {
    this.id = id;
    this.dominio = dominio;
    this.valor = valor;
    this.descricao = descricao;
    this.referencia = referencia;
    this.estado = estado;
  }

  public static Dominio create(String dominio, String valor, String descricao, String referencia, Estado estado) {
    if (dominio == null || dominio.isBlank()) {
      throw new IllegalArgumentException("dominio is mandatory");
    }
    return new Dominio(null, dominio, valor, descricao, referencia, estado);
  }

  public static Dominio rebuild(Long id, String dominio, String valor, String descricao, String referencia, Estado estado) {
    return new Dominio(id, dominio, valor, descricao, referencia, estado);
  }

  public void update(String dominio, String valor, String descricao, String referencia, Estado estado) {
    if (dominio != null && !dominio.isBlank()) {
      this.dominio = dominio;
    }
    this.valor = valor;
    this.descricao = descricao;
    this.referencia = referencia;
    this.estado = estado;
  }
}
