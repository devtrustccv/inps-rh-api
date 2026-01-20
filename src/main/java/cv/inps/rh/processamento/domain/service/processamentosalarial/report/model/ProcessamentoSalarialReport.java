package cv.inps.rh.processamento.domain.service.processamentosalarial.report.model;

import java.time.LocalDate;

public record ProcessamentoSalarialReport(
    String descricaoMovimento,
    LocalDate dataProcessamento,
    String centroDeCusto,
    String cargo,
    Long funId,
    String descricao,
    Long valor,
    Long nif,
    String nomeCargoEscalao,
    Long totalRemuneracoes,
    Long totalDescontos,
    Long totalLiquido
) {
}
