package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record OperationOutcomeResponse(
    Content content
) {

  public record Content(
      String resourceType,
      Issue issue
  ) {
  }

  public record Issue(
      int code,
      String diagnostics
  ) {
  }
}
