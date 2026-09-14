package cv.inps.rh.missaoservico.application.services;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cv.inps.rh.missaoservico.application.services.AvaliacaoPrestadorCalculo.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AvaliacaoPrestadorCalculoTest {

  private BigDecimal total(int sq, int pf, int qp, int cr, int pr) {
    return pontos(PESOS_POR_DEFEITO.get(SISTEMA_QUALIDADE), sq)
        .add(pontos(PESOS_POR_DEFEITO.get(PRAZO_FORNECIMENTO), pf))
        .add(pontos(PESOS_POR_DEFEITO.get(QUALIDADE_PRODUTO), qp))
        .add(pontos(PESOS_POR_DEFEITO.get(CAPACIDADE_RESPOSTA), cr))
        .add(pontos(PESOS_POR_DEFEITO.get(PRECO), pr));
  }

  @Test
  void exemploDaSpecDa95ClasseA() {
    var t = total(100, 100, 100, 100, 75);
    assertEquals(0, t.compareTo(BigDecimal.valueOf(95)));
    assertEquals("A", designacao(t));
  }

  @Test
  void pontosPorCriterio() {
    assertEquals(0, pontos(20, 75).compareTo(BigDecimal.valueOf(15)));
    assertEquals(0, pontos(5, 25).compareTo(new BigDecimal("1.25")));
  }

  @Test
  void limitesDasClasses() {
    assertEquals("B", designacao(BigDecimal.valueOf(75)));
    assertEquals("A", designacao(new BigDecimal("75.01")));
    assertEquals("C", designacao(BigDecimal.valueOf(40)));
    assertEquals("B", designacao(new BigDecimal("40.01")));
    assertEquals("D", designacao(BigDecimal.valueOf(25)));
    assertEquals("C", designacao(new BigDecimal("25.01")));
    assertEquals("D", designacao(BigDecimal.ZERO));
  }

  @Test
  void tudoSatisfazDa50ClasseB() {
    var t = total(50, 50, 50, 50, 50);
    assertEquals(0, t.compareTo(BigDecimal.valueOf(50)));
    assertEquals("B", designacao(t));
  }

  @Test
  void tudoMauDa25ClasseD() {
    assertEquals("D", designacao(total(25, 25, 25, 25, 25)));
  }
}
