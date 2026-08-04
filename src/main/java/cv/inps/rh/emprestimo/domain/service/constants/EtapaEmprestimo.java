package cv.inps.rh.emprestimo.domain.service.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum EtapaEmprestimo {

  PEDIDO("Pedido"),
  ANALISE_RH_PEDIDO("Análise RH - Pedido"),
  ANALISE_RH_ADIANTAMENTO("Análise RH - Adiantamento"),
  ANALISE_RH_REFORCO("Análise RH - Reforço"),
  ANEXAR_CONTRATO_ADIANTAMENTO("Anexar Contrato - Adiantamento"),
  VERIFICACAO_ADIANTAMENTO("Verificação Adiantamento"),
  ANALISE_FINANCEIRA_PEDIDO("Análise Financeira - Pedido"),
  ANALISE_FINANCEIRA_REFORCO("Análise Financeira - Reforço"),
  AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO("Autorizar Comissão Executiva - Pedido"),
  AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO("Autorizar Comissão Executiva - Reforço"),
  ELABORAR_CONTRATO_PEDIDO("Elaborar Contrato - Pedido"),
  ELABORAR_CONTRATO_REFORCO("Elaborar Contrato - Reforço"),
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
