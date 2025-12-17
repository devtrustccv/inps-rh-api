package cv.inps.rh.shared.domain.models;

import java.math.BigDecimal;

public record TipoMovimento(Long id, String descricao, BigDecimal valor, BigDecimal percentagem,
                            String tipo) {

  public static TipoMovimento rebuild(Long id, String descricao, BigDecimal valor, BigDecimal percentagem, String tipo) {
    return new TipoMovimento(id, descricao, valor, percentagem, tipo);
  }
}
