package cv.inps.rh.shared.domain.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TipoMovimento {

  private Long id;
  private String descricao;
  private BigDecimal valor;
  private BigDecimal percentagem;

  private TipoMovimento(Long id, String descricao, BigDecimal valor, BigDecimal percentagem) {
    this.id = id;
    this.descricao = descricao;
    this.valor = valor;
    this.percentagem = percentagem;

  }

  public static TipoMovimento rebuild(Long id, String descricao, BigDecimal valor, BigDecimal percentagem) {
    return new TipoMovimento( id, descricao, valor, percentagem );
  }
}
