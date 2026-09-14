package cv.inps.rh.configuracao.application.services.model;

import java.util.UUID;

public record ResponsavelSectionData(
    Long seccaoId,
    String secaoNome,
    Long responsavelId,
    UUID funcionarioId,
    String email
) {
}
