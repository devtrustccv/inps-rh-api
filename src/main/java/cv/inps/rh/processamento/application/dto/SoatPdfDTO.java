package cv.inps.rh.processamento.application.dto;

import java.util.List;

public record SoatPdfDTO(
    String dataEmissao,
    String referencia,
    String numeroApolice,
    String dataInicioApolice,
    String nomeInstituicao,
    String nifInstituicao,
    String codCae,
    String atividadeEconomica,
    String numeroCertidaoComercial,
    String dataValidadeCertidao,
    String telefone,
    String telemovel,
    String localidade,
    String email,
    String morada,
    String concelho,
    int totalPessoas,
    long massaSalarialAnual,
    List<SoatPdfRowDTO> pessoas
) {
}
