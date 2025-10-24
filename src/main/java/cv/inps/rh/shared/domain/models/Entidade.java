package cv.inps.rh.shared.domain.models;

import lombok.Getter;

@Getter
public class Entidade {

  private final Long id;
  private final String nome;

  private Entidade(
      Long id,
      String nome

  ) {
    this.id = id;
    this.nome = nome;

  }


  public static Entidade rebuild(
      Long id,
      String nome
  ) {
    return new Entidade(
        id,
        nome
    );
  }
}
