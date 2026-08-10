package cv.inps.rh.emprestimo.domain.service.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum TipoPedido {

  AQUISICAO_VIATURA("Aquisição Viatura"),
  FUNDO_SOCIAL("Fundo Social"),
  EMPRESTIMO("Empréstimo"),
  RENEGOCIACAO_DIVIDA("Renegociação Dívida");

  private final String description;

  TipoPedido(String description) {
    this.description = description;
  }

  public static Map<String, String> descriptionMap() {
    return Arrays.stream(values())
        .collect(Collectors.toMap(
            TipoPedido::name,
            TipoPedido::getDescription
        ));
  }
}
