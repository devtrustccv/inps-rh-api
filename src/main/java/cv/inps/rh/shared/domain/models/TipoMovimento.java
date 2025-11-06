package cv.inps.rh.shared.domain.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TipoMovimento {

  private Long id;
  private String descricao;
  private BigDecimal valor;
  private BigDecimal percentagem;
  private String tipo;

  private TipoMovimento(Long id, String descricao, BigDecimal valor, BigDecimal percentagem, String tipo) {
    this.id = id;
    this.descricao = descricao;
    this.valor = valor;
    this.percentagem = percentagem;
    this.tipo = tipo;

  }

  public static TipoMovimento rebuild(Long id, String descricao, BigDecimal valor, BigDecimal percentagem, String tipo) {
    return new TipoMovimento( id, descricao, valor, percentagem, tipo );
  }
}
