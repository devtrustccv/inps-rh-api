package cv.inps.rh.processamento.domain.service.processamentosalarial.api.model;

public record Autorization(
    String UserName,
    String Password,
    String CanalCode,
    String RolesName
) {
}
