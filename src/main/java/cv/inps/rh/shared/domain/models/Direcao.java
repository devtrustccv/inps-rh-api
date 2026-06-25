package cv.inps.rh.shared.domain.models;

public record Direcao(Long id, String nome, String siga) {

  public static Direcao rebuild(Long id, String nome, String siga) {
    return new Direcao(id, nome, siga);
  }

}
