package cv.inps.rh.shared.domain.models;

import lombok.Getter;

@Getter
public class Ups {

  private Long id;
  private String nome;

  private Ups(Long id, String nome) {
    this.id = id;
    this.nome = nome;
  }

  public static Ups rebuild(Long id, String nome) {
    return new Ups(id,  nome);
  }
}
