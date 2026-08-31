package cv.inps.rh.processamento.application.dto;

public record SoatPdfRowDTO(
    String nome,
    String tipoDocumento,
    String numeroDocumento,
    String dataValidadeDocumento,
    Long nif,
    String dataNascimento,
    String sexo,
    String situacao,
    String profissao,
    String aprendizOuEstagiario,
    String horasSemana,
    String unidadeRetribuicao,
    Long retribuicao,
    Long retribuicaoAnual,
    String temporariamenteEstrangeiro,
    String observacoes
) {

}
