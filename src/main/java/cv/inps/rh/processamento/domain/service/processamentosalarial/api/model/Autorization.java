package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

import org.jspecify.annotations.NonNull;

public record Autorization(
    String UserName,
    String Password,
    String CanalCode,
    String RolesName
) {

  /**
   * Os pedidos são registados em debug: a password não pode chegar aos logs.
   */
  @Override
  public @NonNull String toString() {
    return "Autorization[UserName=%s, CanalCode=%s, RolesName=%s]".formatted(UserName, CanalCode, RolesName);
  }
}
