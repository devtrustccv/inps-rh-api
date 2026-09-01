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
    String aprendizEstagiario,
    String horasSemana,
    Long retribuicaoHoraDiaMes,
    String retribuicaoHoraDiarioMensal,
    Long retribuicaoAnual,
    String temporariamenteEstrangeiro,
    String observacoes
) {

}
