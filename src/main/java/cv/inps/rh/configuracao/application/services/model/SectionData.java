package cv.inps.rh.configuracao.application.services.model;

import cv.inps.rh.shared.application.constants.Estado;

import java.util.UUID;

public record SectionData(
    UUID seccaoId,
    String seccaoNome,
    Estado estadoSeccao,
    Long direcaoId,
    String direcaoNome,
    String estadoDirecao
) {
}
