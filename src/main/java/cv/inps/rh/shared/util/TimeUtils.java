package cv.inps.rh.shared.util;

import org.springframework.util.StringUtils;

public class TimeUtils {

  private TimeUtils() {
  }

  public static String formatMinutesToHHmm(Integer minutes) {
    if (minutes == null) {
      return "00:00";
    }
    int h = minutes / 60;
    int m = minutes % 60;

    return String.format("%02d:%02d", h, m);
  }

  public static int hhmmToMinutes(String hhmm) {
    if (!StringUtils.hasText(hhmm)) {
      return 0;
    }

    try {
      var time = java.time.LocalTime.parse(hhmm);
      return time.getHour() * 60 + time.getMinute();
    } catch (Exception _) {
      return 0;
    }
  }


  /**
   * Normaliza um intervalo para {@code HH:MM}, seja qual for a forma em que chega.
   *
   * <p>Aceita {@code "+0 08:00:00"} (o que gravamos), {@code "0 5:20:0.0"} (o que Oracle
   * devolve ao ler um INTERVAL DAY TO SECOND) e {@code "05:20"} (texto já normalizado).
   * Devolve {@code "00:00"} para nulo ou vazio.
   *
   * <p>Antes exigia a componente de dias e rebentava com {@code IllegalArgumentException}
   * perante um simples {@code "05:20"} — o que tornava arriscado usá-la para normalizar
   * valores de origem mista.
   */
  public static String intervalFormatToHHmm(String interval) {
    if (interval == null || interval.isBlank()) {
      return "00:00";
    }

    try {
      return formatMinutesToHHmm((int) toMinutes(interval));
    } catch (Exception e) {
      throw new IllegalArgumentException("Erro ao converter interval para HH:MM: " + interval, e);
    }
  }

  /**
   * Lê uma quantidade de horas em minutos, aceitando as formas que circulam no sistema.
   *
   * <p>Os formulários usam ora um campo numérico ({@code "8"}, {@code "8.5"}), ora um
   * campo de hora ({@code "08:30"}), e a parametrização em BD guarda números simples.
   * Ler tudo como {@code HH:MM} fazia {@code "8.5"} cair silenciosamente para zero —
   * o utilizador escrevia meia hora a mais e o valor ficava a nada.
   *
   * <table>
   *   <tr><td>{@code "8"}</td><td>480 min</td></tr>
   *   <tr><td>{@code "8.5"} ou {@code "8,5"}</td><td>510 min</td></tr>
   *   <tr><td>{@code "08:30"}</td><td>510 min</td></tr>
   * </table>
   *
   * @return minutos; 0 se o valor for vazio ou não interpretável
   */
  public static int parseHorasFlexivel(String valor) {
    if (!StringUtils.hasText(valor)) {
      return 0;
    }

    var v = valor.trim();

    if (v.contains(":")) {
      return (int) toMinutes(v);
    }

    try {
      return (int) Math.round(Double.parseDouble(v.replace(',', '.')) * 60);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Converte uma string "HH:MM" em formato aceito por Oracle INTERVAL DAY TO SECOND.
   * Retorna "+0 HH:MM:SS"
   */
  public static String hhmmToIntervalFormat(String hhmm) {
    if (!StringUtils.hasText(hhmm)) {
      return "+0 00:00:00";
    }

    String[] parts = hhmm.trim().split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Formato inválido de hora: " + hhmm);
    }

    int hours = Integer.parseInt(parts[0]);
    int minutes = Integer.parseInt(parts[1]);

    if (hours < 0 || hours > 23) {
      throw new IllegalArgumentException("Horas inválidas: " + hours);
    }
    if (minutes < 0 || minutes > 59) {
      throw new IllegalArgumentException("Minutos inválidos: " + minutes);
    }

    return String.format("+0 %02d:%02d:00", hours, minutes);
  }

  public static int diffMinutes(String inicio, String fim) {
    if (!StringUtils.hasText(inicio) || !StringUtils.hasText(fim)) {
      return 0;
    }
    try {
      return (int) Math.max(toMinutes(fim) - toMinutes(inicio), 0);
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Converte um intervalo em minutos.
   *
   * <p>Aceita os três formatos que circulam no sistema:
   * <ul>
   *   <li>{@code "+0 HH:MM:SS"} — o que gravamos em Oracle;</li>
   *   <li>{@code "0 14:0:0.0"} — o que Oracle <strong>devolve</strong> ao ler um
   *       INTERVAL DAY TO SECOND: sem sinal, sem zeros à esquerda e com fracção de
   *       segundo;</li>
   *   <li>{@code "HH:MM"} — texto simples.</li>
   * </ul>
   *
   * <p>A versão anterior só removia a componente de dias quando a string começava por
   * {@code "+"}, pelo que a leitura de Oracle caía sempre em excepção e era contada
   * como zero — as horas de dispensa já usadas apareciam sempre a 00:00.
   *
   * <p>A componente de dias é somada, não descartada: um intervalo de {@code "1 02:00:00"}
   * são 26 horas.
   */
  private static long toMinutes(String time) {
    String t = time.trim();

    long dias = 0;

    // Separa a componente de dias, com ou sem sinal: "+0 HH:MM:SS", "0 14:0:0.0"
    int spaceIdx = t.indexOf(' ');
    if (spaceIdx >= 0) {
      String diasPart = t.substring(0, spaceIdx).trim();
      if (diasPart.startsWith("+")) {
        diasPart = diasPart.substring(1);
      }
      boolean negativo = diasPart.startsWith("-");
      if (negativo) {
        diasPart = diasPart.substring(1);
      }
      dias = diasPart.isEmpty() ? 0 : Long.parseLong(diasPart);
      if (negativo) {
        dias = -dias;
      }
      t = t.substring(spaceIdx + 1).trim();
    } else if (t.startsWith("+")) {
      t = t.substring(1);
    }

    String[] parts = t.split(":");
    long hours = Long.parseLong(parts[0].trim());
    // minutos podem vir como "0" ou "00"; segundos e fracção são ignorados
    long minutes = parts.length > 1 ? Long.parseLong(parts[1].trim()) : 0;

    return dias * 24 * 60 + hours * 60 + minutes;
  }


}
