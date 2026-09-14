package cv.inps.rh.shared.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Valores por extenso em português (norma europeia: "dezasseis", "mil milhões"). Usado nos
 * documentos que exigem o montante escrito — ex.: a nota de encomenda da requisição de missão.
 */
public final class ValorPorExtenso {

  private static final String[] UNIDADES = {
      "zero", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
      "onze", "doze", "treze", "catorze", "quinze", "dezasseis", "dezassete", "dezoito", "dezanove"};
  private static final String[] DEZENAS = {
      "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"};
  private static final String[] CENTENAS = {
      "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos",
      "oitocentos", "novecentos"};

  private ValorPorExtenso() {
  }

  /** Montante em escudos, com centavos quando existem — ex.: "cento e cinco mil escudos". */
  public static String escudos(BigDecimal valor) {
    if (valor == null)
      return null;
    var v = valor.setScale(2, RoundingMode.HALF_UP).abs();
    var inteiro = v.longValue();
    var centavos = v.remainder(BigDecimal.ONE).movePointRight(2).intValue();

    var texto = extenso(inteiro) + (inteiro == 1 ? " escudo" : " escudos");
    if (centavos > 0) {
      texto += " e " + extenso(centavos) + (centavos == 1 ? " centavo" : " centavos");
    }
    return valor.signum() < 0 ? "menos " + texto : texto;
  }

  /** Número inteiro por extenso — ex.: 1234 → "mil duzentos e trinta e quatro". */
  public static String extenso(long n) {
    if (n < 0)
      return "menos " + extenso(-n);
    if (n == 0)
      return UNIDADES[0];

    // grupos de três dígitos, do mais significativo para o menos: mil milhões, milhões, milhares, unidades
    long[] grupos = {n / 1_000_000_000L, (n / 1_000_000L) % 1000, (n / 1000L) % 1000, n % 1000};
    var partes = new ArrayList<String>();
    var valores = new ArrayList<Long>();
    for (int i = 0; i < grupos.length; i++) {
      var g = grupos[i];
      if (g == 0)
        continue;
      partes.add(grupo(g, i));
      valores.add(g);
    }

    var sb = new StringBuilder(partes.getFirst());
    for (int i = 1; i < partes.size(); i++) {
      var g = valores.get(i);
      // "mil e duzentos", "mil e cinquenta", mas "mil duzentos e trinta": o "e" só liga o último
      // grupo quando este é menor que cem ou uma centena redonda.
      var ultimo = i == partes.size() - 1;
      sb.append(ultimo && (g < 100 || g % 100 == 0) ? " e " : " ").append(partes.get(i));
    }
    return sb.toString();
  }

  private static String grupo(long g, int escala) {
    return switch (escala) {
      case 0 -> g == 1 ? "mil milhões" : ate999(g) + " mil milhões";
      case 1 -> g == 1 ? "um milhão" : ate999(g) + " milhões";
      case 2 -> g == 1 ? "mil" : ate999(g) + " mil";
      default -> ate999(g);
    };
  }

  private static String ate999(long n) {
    if (n == 100)
      return "cem";
    var partes = new ArrayList<String>(List.of());
    var c = (int) (n / 100);
    var r = (int) (n % 100);
    if (c > 0)
      partes.add(CENTENAS[c]);
    if (r > 0) {
      if (r < 20) {
        partes.add(UNIDADES[r]);
      } else {
        partes.add(r % 10 == 0 ? DEZENAS[r / 10] : DEZENAS[r / 10] + " e " + UNIDADES[r % 10]);
      }
    }
    return String.join(" e ", partes);
  }
}
