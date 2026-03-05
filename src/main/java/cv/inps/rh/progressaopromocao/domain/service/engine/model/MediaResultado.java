package cv.inps.rh.progressaopromocao.domain.service.engine.model;

public record MediaResultado(
    Double media,
    boolean elegivelProgressao,
    boolean elegivelPromocao
) {

  public static MediaResultado invalido() {
    return new MediaResultado(
        0.0,
        false,
        false
    );
  }
}
