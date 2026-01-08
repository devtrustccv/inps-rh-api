package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record ProcessarCabimentoRequest(
    OperationRequest OperationRequest
) {

  public record OperationRequest(
      RequestHeader RequestHeader,
      RequestBody RequestBody
  ) {
  }

  public record User(Integer UserID) {
  }

  public record RequestBody(ProcessaCabimento processa_cabimento) {
  }

  public record ProcessaCabimento(
      String p_proc_sal_id,
      String p_data_pag,
      String p_sessid
  ) {
  }
}
