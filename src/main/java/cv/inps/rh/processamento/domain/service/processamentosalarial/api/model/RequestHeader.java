package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record RequestHeader(
    Autorization Autorization,
    User User
) {

  public RequestHeader() {
    this(new Autorization("sips", "sips@is", "1001", "sips"), new User(0));
  }
}
