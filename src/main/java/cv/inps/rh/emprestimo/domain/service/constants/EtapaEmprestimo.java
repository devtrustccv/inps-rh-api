package cv.inps.rh.emprestimo.domain.service.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum EtapaEmprestimo {

  // Nomes mostrados na lista (coluna Etapa) e nas mensagens: v2 / melhoria 11/08 — "Contrato",
  // "Amortização" e "Renegociação" (antes "Elaborar Contrato - Pedido", "Adiantamento", "Reforço").
  PEDIDO("Pedido"),
  ANALISE_RH_PEDIDO("Análise RH"),
  ANALISE_RH_ADIANTAMENTO("Análise RH - Amortização"),
  ANALISE_RH_REFORCO("Análise RH - Renegociação"),
  ANEXAR_CONTRATO_ADIANTAMENTO("Anexar Comprovativo - Amortização"),
  VERIFICACAO_ADIANTAMENTO("Verificação - Amortização"),
  ANALISE_FINANCEIRA_PEDIDO("Análise Financeira"),
  ANALISE_FINANCEIRA_REFORCO("Análise Financeira - Renegociação"),
  AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO("Autorização Comissão Executiva"),
  AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO("Autorização Comissão Executiva - Renegociação"),
  ELABORAR_CONTRATO_PEDIDO("Contrato"),
  ELABORAR_CONTRATO_REFORCO("Contrato - Renegociação"),
  PAGAMENTO("Pagamento");

  private final String description;

  EtapaEmprestimo(String description) {
    this.description = description;
  }

  public static Map<String, String> descriptionMap() {
    return Arrays.stream(values())
        .collect(Collectors.toMap(
            EtapaEmprestimo::name,
            EtapaEmprestimo::getDescription
        ));
  }
}
