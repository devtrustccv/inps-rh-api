package cv.inps.rh.emprestimo.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Confirmação do pagamento de um empréstimo Cabimentado (manual, enquanto o job de confirmação
 * não existe). Passa o empréstimo a Ativo.
 */
public record ConfirmarPagamentoData(
    @NotNull(message = "A data do pagamento é obrigatória")
    LocalDate dataPagamento,

    String observacao,

    List<DocumentoDTO> files
) {

}
