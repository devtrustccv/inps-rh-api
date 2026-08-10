package cv.inps.rh.emprestimo.domain.service.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum StatusEmprestimo {

  POR_SUBMETER("Por Submeter"),
  SUBMETIDO("Submetido"),
  CANCELADO("Cancelado"),
  VALIDADO_RH("Validado RH"),
  VALIDADO_DFI("Validado DFI"),
  AUTORIZADO("Autorizado"),
  NAO_AUTORIZADO("Não Autorizado"),
  EM_CORRECAO("Em Correção"),
  CABIMENTADO("Cabimentado"),
  AGUARDA_PAGAMENTO("Aguarda Pagamento"),
  ATIVO("Ativo"),
  ARQUIVADO("Arquivado");

  private final String description;

  StatusEmprestimo(String description) {
    this.description = description;
  }

  public static Map<String, String> codeDescriptionMap() {
    return Arrays.stream(values())
        .collect(Collectors.toMap(
            StatusEmprestimo::name,
            StatusEmprestimo::getDescription
        ));
  }
}
