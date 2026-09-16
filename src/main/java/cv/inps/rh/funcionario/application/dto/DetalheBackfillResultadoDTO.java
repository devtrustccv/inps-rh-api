package cv.inps.rh.funcionario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resultado do backfill de {@code RH_T_VALIDACAO_DETALHE} a partir do histórico do JaVers.
 *
 * <p><b>Temporário:</b> existe só para migrar o detalhe das validações antigas antes de as tabelas
 * {@code JV_*} serem largadas; sai com o JaVers.
 */
@Schema(description = "Resultado do backfill do detalhe de alterações")
public record DetalheBackfillResultadoDTO(
    @Schema(description = "true = só contou, não gravou nada") boolean dryRun,
    @Schema(description = "Validações sem detalhe congelado que foram analisadas") int validacoesAnalisadas,
    @Schema(description = "Das analisadas, as que o JaVers conseguiu descrever") int validacoesComLinhas,
    @Schema(description = "Das analisadas, as que o JaVers devolveu vazias (nada a copiar)") int validacoesVazias,
    @Schema(description = "Linhas gravadas (ou que seriam gravadas, em dry-run)") int linhas,
    @Schema(description = "Detalhe por referência") List<PorReferencia> porReferencia,
    @Schema(description = "Validações que falharam, com o motivo") List<String> erros) {

  @Schema(description = "Contagem por referência de validação")
  public record PorReferencia(String referencia, int validacoes, int vazias, int linhas) {}
}
