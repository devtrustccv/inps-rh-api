package cv.inps.rh.shared.domain.models;

public record Instituicao(Long id, String nome, String codigo) {

  public static Instituicao rebuild(Long id, String nome, String codigo) {
    return new Instituicao(id, nome, codigo);
  }

}
