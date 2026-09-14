package cv.inps.rh.missaoservico.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Regras de cálculo da avaliação de fornecedor (spec 14/09, "Avaliar Prestador"). Cada critério tem
 * um peso (%) e recebe uma avaliação em percentagem (Muito Bom 100, Bom 75, Satisfaz 50, Mau 25);
 * os pontos são peso × percentagem e o total é a soma. A classe sai do total.
 */
public final class AvaliacaoPrestadorCalculo {

  public static final String SISTEMA_QUALIDADE = "SISTEMA_QUALIDADE";
  public static final String PRAZO_FORNECIMENTO = "PRAZO_FORNECIMENTO";
  public static final String QUALIDADE_PRODUTO = "QUALIDADE_PRODUTO";
  public static final String CAPACIDADE_RESPOSTA = "CAPACIDADE_RESPOSTA";
  public static final String PRECO = "PRECO";

  public static final List<String> CRITERIOS =
      List.of(SISTEMA_QUALIDADE, PRAZO_FORNECIMENTO, QUALIDADE_PRODUTO, CAPACIDADE_RESPOSTA, PRECO);

  /** Pesos da spec — usados quando o domínio AVALIACAO_FORNECEDOR (referência PESO) não os define. */
  public static final Map<String, Integer> PESOS_POR_DEFEITO = Map.of(
      SISTEMA_QUALIDADE, 5,
      PRAZO_FORNECIMENTO, 15,
      QUALIDADE_PRODUTO, 40,
      CAPACIDADE_RESPOSTA, 20,
      PRECO, 20);

  private AvaliacaoPrestadorCalculo() {
  }

  /** Pontos de um critério — ex.: peso 20 com "Bom" (75) → 15. */
  public static BigDecimal pontos(int peso, int percentagem) {
    return BigDecimal.valueOf(peso)
        .multiply(BigDecimal.valueOf(percentagem))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  /** Classe do fornecedor: A &gt; 75, B ]40;75], C ]25;40], D [0;25]. */
  public static String designacao(BigDecimal total) {
    if (total.compareTo(BigDecimal.valueOf(75)) > 0)
      return "A";
    if (total.compareTo(BigDecimal.valueOf(40)) > 0)
      return "B";
    if (total.compareTo(BigDecimal.valueOf(25)) > 0)
      return "C";
    return "D";
  }
}
