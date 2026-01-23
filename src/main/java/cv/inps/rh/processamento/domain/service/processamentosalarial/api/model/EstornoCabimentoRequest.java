package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EstornoCabimentoRequest(
    OperationRequest OperationRequest
) {

  public record OperationRequest(
      RequestHeader RequestHeader,
      RequestBody RequestBody
  ) {
  }

  public record RequestBody(
      @JsonProperty("trata_estorno_cabimento")
      TrataEstornoCabimento trataEstornoCabimento
  ) {
  }

  public record TrataEstornoCabimento(
      @JsonProperty("sessid")
      String sessId,

      @JsonProperty("p_codigo")
      String codigo
  ) {
  }

}
