package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record AutorizaSalarioRequest(
    OperationRequest OperationRequest
) {

  public record OperationRequest(
      RequestHeader RequestHeader,
      RequestBody RequestBody
  ) {
  }

  public record RequestBody(
      AutorizaProcessoSalario autoriza_processo_salario
  ) {
  }

  public record AutorizaProcessoSalario(
      String p_codigo,
      String p_visa,
      String p_obs,
      String p_sessid,
      String p_transaccao,
      String p_utilizador
  ) {
  }
}
