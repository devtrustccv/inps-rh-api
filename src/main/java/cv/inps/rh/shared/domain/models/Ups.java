package cv.inps.rh.shared.domain.models;

public record Ups(Long id, String nome) {

  public static Ups rebuild(Long id, String nome) {
    return new Ups(id, nome);
  }
}
