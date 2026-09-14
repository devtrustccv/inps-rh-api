package cv.inps.rh.shared.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValorPorExtensoTest {

  @Test
  void unidadesEDezenas() {
    assertEquals("zero", ValorPorExtenso.extenso(0));
    assertEquals("um", ValorPorExtenso.extenso(1));
    assertEquals("dezasseis", ValorPorExtenso.extenso(16));
    assertEquals("vinte", ValorPorExtenso.extenso(20));
    assertEquals("vinte e um", ValorPorExtenso.extenso(21));
  }

  @Test
  void centenas() {
    assertEquals("cem", ValorPorExtenso.extenso(100));
    assertEquals("cento e um", ValorPorExtenso.extenso(101));
    assertEquals("duzentos e trinta e quatro", ValorPorExtenso.extenso(234));
  }

  @Test
  void milharesComERegraDoE() {
    assertEquals("mil", ValorPorExtenso.extenso(1000));
    assertEquals("mil e duzentos", ValorPorExtenso.extenso(1200));
    assertEquals("mil e cinquenta", ValorPorExtenso.extenso(1050));
    assertEquals("mil duzentos e trinta e quatro", ValorPorExtenso.extenso(1234));
    assertEquals("cento e cinco mil", ValorPorExtenso.extenso(105000));
  }

  @Test
  void milhoes() {
    assertEquals("um milhão", ValorPorExtenso.extenso(1_000_000));
    assertEquals("um milhão e um", ValorPorExtenso.extenso(1_000_001));
    assertEquals("dois milhões e quinhentos mil", ValorPorExtenso.extenso(2_500_000));
    assertEquals("mil milhões", ValorPorExtenso.extenso(1_000_000_000L));
  }

  @Test
  void escudos() {
    assertEquals("cento e cinco mil escudos", ValorPorExtenso.escudos(new BigDecimal("105000")));
    assertEquals("um escudo", ValorPorExtenso.escudos(BigDecimal.ONE));
    assertEquals("doze escudos e cinquenta centavos", ValorPorExtenso.escudos(new BigDecimal("12.50")));
    assertNull(ValorPorExtenso.escudos(null));
  }
}
