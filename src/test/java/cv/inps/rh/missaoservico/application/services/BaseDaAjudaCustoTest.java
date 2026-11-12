package cv.inps.rh.missaoservico.application.services;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoServiceRead.baseDaAjudaCusto;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** MS-013: a base da ajuda de custo reconstruída a partir do efectivo gravado (base × fracção). */
class BaseDaAjudaCustoTest {

  private static void base(String efectivo, String fracao, String esperado) {
    assertEquals(0, baseDaAjudaCusto(new BigDecimal(efectivo), fracao).compareTo(new BigDecimal(esperado)),
        efectivo + " / " + fracao);
  }

  @Test
  void semFracaoEOProprioValor() {
    base("3500", "1", "3500");
    base("4000.55", "1", "4000.55");
  }

  @Test
  void umTercoNaoArrastaOArredondamento() {
    base("666.67", "1/3", "2000");    // 666.67 × 3 = 2000.01 na inversão simples
    base("1166.67", "1/3", "3500");
  }

  @Test
  void doisTercos() {
    base("1333.33", "2/3", "2000");
    base("2333.33", "2/3", "3500");
    base("2000", "2/3", "3000");
  }

  @Test
  void baseComCentimosERecuperada() {
    base("3333.34", "2/3", "5000.01");  // 5000.01 × ⅔ = 3333.34
  }
}
