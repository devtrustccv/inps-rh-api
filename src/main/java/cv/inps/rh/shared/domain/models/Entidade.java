package cv.inps.rh.shared.domain.models;

public record Entidade(Long id, String nome) {


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
