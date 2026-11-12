package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record OperationOutcomeResponse(
    Content content
) {

  private static final int CODE_SUCCESS = 200;

  public boolean isSuccess() {
    return content != null && content.issue() != null && content.issue().code() == CODE_SUCCESS;
  }

  public String diagnostics() {
    return content != null && content.issue() != null ? content.issue().diagnostics() : null;
  }

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
