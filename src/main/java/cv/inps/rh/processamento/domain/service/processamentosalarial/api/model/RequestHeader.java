package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestHeader {

  private ProcessarCabimentoRequest.Autorization Autorization;
  private ProcessarCabimentoRequest.User User;

  public RequestHeader() {
    this.Autorization = new ProcessarCabimentoRequest.Autorization(
        "sips",
        "sips@is",
        "1001",
        "sips"
    );
    this.User = new ProcessarCabimentoRequest.User(0);
  }
}
