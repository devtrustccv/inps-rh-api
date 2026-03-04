package cv.inps.rh.progressaopromocao.domain.service.engine.model;

public record MediaResultado(
    double media,
    boolean elegivelProgressao,
    boolean elegivelPromocao
) {

  public static MediaResultado invalido() {
    return new MediaResultado(
        0,
        false,
        false
    );
  }
}
